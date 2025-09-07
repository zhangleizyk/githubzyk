package com.zyk.consumer.controller;

import com.zyk.consumer.service.HelloClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/NewHello")
@Api(tags = "Eureka客户端")
public class HelloClientController {

    @Autowired
    private HelloClientService helloClientService;

    @ApiOperation(value = "获取信息", notes = "获取信息1")
    @GetMapping("/getClient")
    public String getClient(){
        return helloClientService.getProduct();
    }
}
