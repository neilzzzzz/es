package com.ydles.search.listener;

import com.ydles.search.config.RabbitMQConfig;
import com.ydles.search.service.EsManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@Component
public class GoodsDelListener {
    @Autowired
    EsManagerService esManagerService;

    @RabbitListener(queues = RabbitMQConfig.SEARCH_DEL_QUEUE)
    public void recieveMsg(String spuId){
        System.out.println("商品下架了："+spuId);
        esManagerService.delDataBySpuId(spuId);
    }

}
