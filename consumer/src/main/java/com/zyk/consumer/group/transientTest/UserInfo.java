package com.zyk.consumer.group.transientTest;

import java.io.Serializable;
import java.util.*;

public class UserInfo implements Serializable {
    private static String name;
    private final  String pwd;

    public UserInfo(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    public  String getName() {
        return name;
    }
    public  void setName(String name) {
        this.name = name;
    }
    public String getPsw() {
        return pwd;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }

    public static void main(String[] args) {
        List<String> omt = new ArrayList<>();
        Collections.unmodifiableCollection(omt);
        omt.add("3");
        Map<String, String> map = new HashMap<>();
    }
}
