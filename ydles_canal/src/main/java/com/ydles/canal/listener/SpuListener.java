package com.ydles.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.ydles.canal.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@CanalEventListener //这个类是canal的监听类
public class SpuListener {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @ListenPoint(schema = "ydles_goods",table = "tb_spu")
    public void goodsUp(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        System.out.println("监听到了spu数据变了："+eventType);

        //isMarketable 0->1
        //改变之前的数据 --->map  {id:123,name:prada}
        Map<String, String> oldMap=new HashMap<>();
        rowData.getBeforeColumnsList().forEach(column -> oldMap.put(column.getName(), column.getValue()));
        //改变之后的数据 --->map  {id:123,name:prada}
        Map<String, String> newMap=new HashMap<>();
        rowData.getAfterColumnsList().forEach(column -> newMap.put(column.getName(), column.getValue()));

        //监听到商品上架
        if (oldMap.get("is_marketable").equals("0")&& newMap.get("is_marketable").equals("1")){
            //mq发spuId
            String spuId = newMap.get("id");
            System.out.println("商品上架了，往mq发消息"+spuId);
            rabbitTemplate.convertAndSend(RabbitMQConfig.GOODS_UP_EXCHANGE,"",spuId);
        }

        //监听到商品下架
        if (oldMap.get("is_marketable").equals("1")&& newMap.get("is_marketable").equals("0")){
            //mq发spuId
            String spuId = newMap.get("id");
            System.out.println("商品下架了，往mq发消息"+spuId);
            rabbitTemplate.convertAndSend(RabbitMQConfig.GOODS_DOWN_EXCHANGE,"",spuId);
        }



    }

}
