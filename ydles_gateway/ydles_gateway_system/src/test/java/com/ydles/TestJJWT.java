package com.ydles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
public class TestJJWT {
    public static void main(String[] args) {
        //设置 jwt令牌1小时存活时间
        Date date = new Date();

        JwtBuilder jwtBuilder = Jwts.builder()
                .setId("27")//设置令牌id
                .setSubject("ydlersheshangcheng")//主题
                .setIssuedAt(new Date())//签发时间
                //.setExpiration(new Date())//过期时间
                .claim("name", "itlils")
                .claim("age", 18)//自定义信息放进jwt
                .signWith(SignatureAlgorithm.HS256, "ydlershe");//签名和秘钥

        String jwt = jwtBuilder.compact();
        System.out.println(jwt);

        //解密
        Claims claims = Jwts.parser().setSigningKey("ydlershe").parseClaimsJws(jwt).getBody();
        System.out.println(claims);


    }


}
