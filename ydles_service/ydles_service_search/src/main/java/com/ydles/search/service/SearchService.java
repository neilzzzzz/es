package com.ydles.search.service;

import com.ydles.search.pojo.SkuInfo;

import java.util.List;
import java.util.Map;

/** 搜索相关
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
public interface SearchService {
    /**
     * 搜索方法
     * @param searchMap 搜索参数
     * @return 搜索结果
     */
    public Map search(Map<String,String> searchMap);
}
