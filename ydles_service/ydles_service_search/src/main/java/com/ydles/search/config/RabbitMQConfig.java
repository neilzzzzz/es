package com.ydles.search.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@Configuration
public class RabbitMQConfig {
    //定义队列名称
    public static final String AD_UPDATE_QUEUE="ad_update_queue";

    @Bean
    public Queue queue(){
        return new Queue(AD_UPDATE_QUEUE,true);
    }

    //上架交换机名称
    public static final String GOODS_UP_EXCHANGE = "goods_up_exchange";
    //定义上架队列名称
    public static final String SEARCH_ADD_QUEUE = "search_add_queue";

    //定义上架队列
    @Bean(SEARCH_ADD_QUEUE)
    public Queue SEARCH_ADD_QUEUE(){
        return new Queue(SEARCH_ADD_QUEUE);
    }

    //定义上架交换机
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE(){
        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true)
                .build();
    }

    //上架队列和上架交换机绑定关系构建
    @Bean
    public Binding GOODS_UP_EXCHANGE_SEARCH_ADD_QUEUE(
            @Qualifier(SEARCH_ADD_QUEUE)Queue queue
            ,@Qualifier(GOODS_UP_EXCHANGE)Exchange exchange ){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    //下架交换机名称
    public static final String GOODS_DOWN_EXCHANGE = "goods_down_exchange";
    //定义下架队列名称
    public static final String SEARCH_DEL_QUEUE = "search_del_queue";

    //定义下架队列
    @Bean(SEARCH_DEL_QUEUE)
    public Queue SEARCH_DEL_QUEUE(){
        return new Queue(SEARCH_DEL_QUEUE);
    }

    //定义下架交换机
    @Bean(GOODS_DOWN_EXCHANGE)
    public Exchange GOODS_DOWN_EXCHANGE(){
        return ExchangeBuilder.fanoutExchange(GOODS_DOWN_EXCHANGE).durable(true)
                .build();
    }

    //下架队列和下架交换机绑定关系构建
    @Bean
    public Binding GOODS_DOWN_EXCHANGE_SEARCH_DEL_QUEUE(
            @Qualifier(SEARCH_DEL_QUEUE)Queue queue
            ,@Qualifier(GOODS_DOWN_EXCHANGE)Exchange exchange ){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

}
