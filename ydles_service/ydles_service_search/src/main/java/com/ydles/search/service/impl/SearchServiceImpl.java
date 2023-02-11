package com.ydles.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.search.pojo.SkuInfo;
import com.ydles.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
//缺啥补啥
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    //搜索方法
    @Override
    public Map search(Map<String, String> searchMap) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(searchMap!=null){
            //多个搜索条件 bool
            if(StringUtils.isNotEmpty(searchMap.get("keywords"))){
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keywords"));
                boolQueryBuilder.must(matchQueryBuilder);
            }
            //品牌查询
            if(StringUtils.isNotEmpty(searchMap.get("brand"))){
                //brandName keyword term查询
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
                //term查询一般放到 filter
                boolQueryBuilder.filter(termQueryBuilder);
            }
            //规格查询  spec_二手程度-》战痕累累%2B6程新  spec_颜色-》黑色
            for (String key : searchMap.keySet()) {
                if(key.startsWith("spec_")){
                    String value = searchMap.get(key).replace("%2B", "+"); //战痕累累
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", value);
                    boolQueryBuilder.filter(termQueryBuilder);
                }
            }
            //价格区间查询  price=0-5000     price=30000
            if(StringUtils.isNotEmpty(searchMap.get("price"))){
                String price = searchMap.get("price");
                String[] split = price.split("-");

                //代码优化 codeReview
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(split[0]);
                if(split.length==2){
                    //price=0-5000
                    rangeQueryBuilder.lte(split[1]);
                }//else {
                    //price=30000
                //}
                boolQueryBuilder.filter(rangeQueryBuilder);
            }

        }
        //搜索
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        //聚合 品牌
        String skuBrand="skuBrand";
        TermsAggregationBuilder brandTerms = AggregationBuilders.terms(skuBrand).field("brandName");
        nativeSearchQueryBuilder.addAggregation(brandTerms);
        //聚合 规格
        String skuSpec="skuSpec";
        TermsAggregationBuilder specTerms = AggregationBuilders.terms(skuSpec).field("spec.keyword");
        nativeSearchQueryBuilder.addAggregation(specTerms);

        //分页  pageNum=1&pageSize=30
        String pageNum = searchMap.get("pageNum");
        String pageSize = searchMap.get("pageSize");
        if(StringUtils.isEmpty(pageNum)){
            pageNum="1";
        }
        if(StringUtils.isEmpty(pageSize)){
            pageSize="30";
        }
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(pageNum)-1, Integer.parseInt(pageSize));
        nativeSearchQueryBuilder.withPageable(pageRequest);

        //排序
        if(StringUtils.isNotEmpty(searchMap.get("sortField"))&&StringUtils.isNotEmpty(searchMap.get("sortRule"))){
            if(searchMap.get("sortRule").equals("ASC")){
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.ASC));
            }else {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.DESC));
            }
        }

        //设置高亮
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");
        //前签
        field.preTags("<span style='color:red'>");
        //后签
        field.postTags("</span>");

        nativeSearchQueryBuilder.withHighlightFields(field);

        /**
         *  1nativeSearchQuery  承接搜索条件
         *  2SkuInfo.class 实体类类型
         *  3SearchResultMapper 结果集，对象 映射
         *
         *  skuInfos 查询结果
         */
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                    //搜索结果和对象如何映射
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                        List<T> list=new ArrayList<>();

                        SearchHits hits = searchResponse.getHits(); //大HITS
                        //总数
                        long totalHits = hits.getTotalHits();

                        SearchHit[] hits1 = hits.getHits();
                        for (SearchHit hit : hits1) {
                            //hit----->SkuInfo对象
                            String sourceAsString = hit.getSourceAsString(); //"{id:123,name:prada,price:8000}"
                            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);

                            //获取高亮 设置到name属性中
                            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                            HighlightField highlightField = highlightFields.get("name");
                            Text[] fragments = highlightField.getFragments();
                            String realName="";
                            for (Text fragment : fragments) {
                                realName+=fragment; //jdk 底层
                            }
                            skuInfo.setName(realName);


                            list.add((T) skuInfo);
                        }

                        return new AggregatedPageImpl<>(list, pageable, totalHits, searchResponse.getAggregations());
                    }
                }
        );


        //1封装最终返回结果
        Map resultMap=new HashMap();
        //总记录数
        resultMap.put("total", skuInfos.getTotalElements());
        //总页数
        resultMap.put("totalpages",skuInfos.getTotalPages());
        //数据集合
        resultMap.put("rows",skuInfos.getContent());

        //把品牌聚合结果前端返回
        StringTerms brandStringTerms = (StringTerms) skuInfos.getAggregation(skuBrand);
        List<String> brandList = brandStringTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
        resultMap.put("brandList",brandList);
        //把规格聚合结果前端返回
        StringTerms specStringTerms = (StringTerms) skuInfos.getAggregation(skuSpec);
        //bucket  {
        //          "key": "{'颜色': '黑色'}",
        //          "doc_count": 314
        //        }
        List<String> specList = specStringTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());


        resultMap.put("specList",formatSpec(specList));

        //当前页 每页多少
        resultMap.put("pageNum",pageNum);//当前第几页
        resultMap.put("pageSize",pageSize);//页大小


        return resultMap;
    }

    /**
     * 规格转化方法
     * [
     * "{'颜色': '蓝色', '版本': '6GB+128GB'}",
     * "{'颜色': '黑色', '版本': '6GB+128GB'}",
     * "{'颜色': '黑色', '版本': '4GB+64GB'}",
     * "{'颜色': '蓝色', '版本': '4GB+64GB'}",
     * "{'颜色': '蓝色', '版本': '6GB+64GB'}",
     * "{'颜色': '黑色', '版本': '6GB+64GB'}",
     * "{'颜色': '黑色'}",
     * "{'颜色': '蓝色'}",
     * "{'颜色': '金色', '版本': '4GB+64GB'}",
     * "{'颜色': '粉色', '版本': '6GB+128GB'}"
     * ]
     *
     * ======》颜色：【蓝色，黑色，金色，粉色】
     *        版本：【6GB+128GB,4GB+64GB】
     */
    public Map<String, Set<String>> formatSpec(List<String> specList){
        Map<String, Set<String>> resultMap=new HashMap<>();
        //遍历list
        for (String specStr : specList) {
            //specStr ====> "{'颜色': '蓝色', '版本': '6GB+128GB'}"
            Map<String, String> specMap = JSON.parseObject(specStr, Map.class); //颜色:蓝色 版本:6GB+128GB
            //遍历map
            for (String key : specMap.keySet()) {
                //key 颜色    value 蓝色
                Set<String> valueSet = resultMap.get(key);  //valueSet:【蓝色，黑色，金色，粉色】
                if(valueSet==null){
                    valueSet=new HashSet<>();
                }
                valueSet.add(specMap.get(key));

                resultMap.put(key,valueSet);
            }
        }

        return resultMap;
    }
}
