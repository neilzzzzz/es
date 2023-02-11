package com.ydles;

import redis.clients.jedis.Jedis;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
public class RedisTest {
    public static void main(String[] args) {
        Jedis jedis=new Jedis("192.168.200.128",6379);
        for (int i = 0; i < 10; i++) {
            Long id = jedis.incr("id");
            System.out.println(id);
        }

    }
}
