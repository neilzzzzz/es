package com.ydles.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.ydles.canal.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@CanalEventListener //这个类是canal的监听类
public class BusinessListener {
    @Autowired
    RabbitTemplate rabbitTemplate;
    /**
     * 数据监控微服务，监控tb_ad表，当发生增删改操作时，提取position值（广告位置key）， 发送到rabbitmq
     * @param eventType 改变 insert update delete
     * @param rowData
     */
    @ListenPoint(schema = "ydles_business",table = "tb_ad")
    public void adUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        System.out.println("监听到ad表数据变了！");
        System.out.println("EventType:"+eventType);

        //改变之前 这一行数据
        rowData.getBeforeColumnsList().forEach(c->System.out.println("改变之前，列名字："+c.getName()+"列值："+c.getValue()));

        System.out.println("======================================");

        //改变之后 这一行数据
        //rowData.getAfterColumnsList().forEach(c->System.out.println("改变之后，列名字："+c.getName()+"列值："+c.getValue()));
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            if(column.getName().equals("position")){
                String value = column.getValue(); //web_index_lb
                System.out.println("广告数据发送更新的消息了："+value);
                //往mq发消息
                rabbitTemplate.convertAndSend("",RabbitMQConfig.AD_UPDATE_QUEUE,value);
            }
        }

    }




}
