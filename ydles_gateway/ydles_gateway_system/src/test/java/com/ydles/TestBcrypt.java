package com.ydles;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
public class TestBcrypt {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            String hashpw = BCrypt.hashpw("123456", BCrypt.gensalt());
            System.out.println(hashpw);

            boolean checkpw = BCrypt.checkpw("123456", hashpw);
            System.out.println(checkpw);
        }



    }
}
