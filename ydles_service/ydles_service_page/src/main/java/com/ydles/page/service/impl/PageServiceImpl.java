package com.ydles.page.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.entity.Result;
import com.ydles.goods.feign.CategoryFeign;
import com.ydles.goods.feign.SkuFeign;
import com.ydles.goods.feign.SpuFeign;
import com.ydles.goods.pojo.Category;
import com.ydles.goods.pojo.Sku;
import com.ydles.goods.pojo.Spu;
import com.ydles.page.service.PageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 *
 * 缺啥补啥
 */
@Service
public class PageServiceImpl implements PageService {
    @Autowired
    TemplateEngine templateEngine;
    @Value("${pagepath}")
    String pagepath;

    //生成静态化页面的方法
    @Override
    public void generateHtml(String spuId) {
        //1 上下文 包含数据
        Context context=new Context();
        //context.setVariable("name","value");
        Map<String, Object> dataMap = getData(spuId);
        context.setVariables(dataMap);

        //2文件
        File dir = new File(pagepath); //文件夹   d:/item
        //如果文件夹不存在 创建
        if(!dir.exists()){
            dir.mkdirs();
        }

        //d:/item/spuid.html
        File file = new File(pagepath + "/" + spuId + ".html");

        Writer writer=null;
        try {
            writer=new FileWriter(file);
            templateEngine.process("item",context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Autowired
    SpuFeign spuFeign;
    @Autowired
    SkuFeign skuFeign;
    @Autowired
    CategoryFeign categoryFeign;

    //获取数据方法
    public Map<String,Object> getData(String spuId){
        Map<String,Object> resultMap = new HashMap<>();

        //1spu
        Spu spu = spuFeign.findSpuById(spuId).getData();
        resultMap.put("spu",spu);
        //4imageList
        String images = spu.getImages();
        if(StringUtils.isNotEmpty(images)){
            String[] imageList = images.split(",");
            resultMap.put("imageList",imageList);
        }
        //5 specificationList
        String specItems = spu.getSpecItems();
        if(StringUtils.isNotEmpty(specItems)){
            Map specMap = JSON.parseObject(specItems, Map.class);
            resultMap.put("specificationList",specMap);
        }


        //2sku
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        resultMap.put("skuList",skuList);

        //3category
        Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
        Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
        Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();
        resultMap.put("category1",category1);
        resultMap.put("category2",category2);
        resultMap.put("category3",category3);

        return resultMap;
    }
}
