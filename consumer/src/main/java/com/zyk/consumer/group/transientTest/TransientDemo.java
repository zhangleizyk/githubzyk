package com.zyk.consumer.group.transientTest;

import java.io.*;

public class TransientDemo {

    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo("张三","123456");
        System.out.println("序列化之前：" + userInfo);
        /*
         * 序列化
         */
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("userinfo.txt"));
            output.writeObject(new UserInfo("张三","123456"));
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * 反序列化
         */
        try {
            userInfo.setName("李四");
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("userinfo.txt"));
            Object obj = input.readObject();
            System.out.println("序列化之后：" + obj);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
