package com.ydles.search.service;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
public interface EsManagerService {

    //1创建索引库，映射
    public void createIndexAndMapping();

    //2 现有所有sku导入es
    public void importData();

    //3 根据spuId,将他的skuList导入es
    public void importDataBySpuId(String spuId);

    //4根据spuid删除es索引库中相关的sku数据
    void delDataBySpuId(String spuId);

}
