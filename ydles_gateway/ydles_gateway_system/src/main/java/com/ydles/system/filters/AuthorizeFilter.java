package com.ydles.system.filters;

import com.ydles.system.util.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 *  缺啥补啥
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //0获取到请求和响应
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getURI().getPath();
        if(path.contains("admin/login")){
            //1admin 登陆 放行
            return chain.filter(exchange);
        }

        //2访问资源
        //什么状态是登陆？  header token=>jwt
        HttpHeaders headers = request.getHeaders();
        String jwt = headers.getFirst(AUTHORIZE_TOKEN);
        //2.1没有jwt 拒绝
        if(StringUtils.isEmpty(jwt)){
            //拒绝
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //2.2带着jwt 校验
        try {
            JwtUtil.parseJWT(jwt);
        }catch (Exception e){
            //logger.warn
            //拒绝
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
