package com.ydles.system.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */

@Component //交由spring创建
public class IpFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //业务逻辑
        //黑名单ip拦截
        //拿到请求和响应，为所欲为
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String hostString = remoteAddress.getHostString();
        System.out.println("hostString："+hostString);
        String hostName = remoteAddress.getHostName();
        System.out.println("hostName:"+hostName);

        if(hostString.equals("192.168.31.17")){
            //拒绝访问
            response.setStatusCode(HttpStatus.FORBIDDEN); //403 禁止访问
            return response.setComplete();
        }

        //chain.filter(exchange); //过滤器链 往后走吧
        //放行
        return chain.filter(exchange);
    }

    //指定过滤器顺序 越小越先执行
    @Override
    public int getOrder() {
        return 1;
    }
}
