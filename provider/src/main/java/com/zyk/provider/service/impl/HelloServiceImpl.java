package com.zyk.provider.service.impl;

import com.zyk.provider.service.HelloService;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public String getHello() {
        return "你好兄弟";
    }
}
