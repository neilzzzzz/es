package com.ydles;

import java.util.UUID;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
public class UUIDTest {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            String s = UUID.randomUUID().toString();
            System.out.println(s);

            //34f31e4a-ee07-4976-8ce0-375ba9237c97 包包
        }
    }

}
