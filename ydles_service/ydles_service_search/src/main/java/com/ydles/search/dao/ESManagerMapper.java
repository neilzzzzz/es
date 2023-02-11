package com.ydles.search.dao;

import com.ydles.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
public interface ESManagerMapper extends ElasticsearchRepository<SkuInfo,Long> {

}
