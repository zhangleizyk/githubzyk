package com.zyk.consumer.controller;

import com.zyk.consumer.bo.ShoppingBo;
import com.zyk.consumer.service.ShoppingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/v1/lift/shop")
@Api(tags = "购物清单")
public class ShoppingController {
    @Resource
    private ShoppingService shoppingService;

    @GetMapping("/get")
    @ApiOperation(value = "查询购物清单", notes = "查询购物清单")
    public List<ShoppingBo> getShoppingList(ShoppingBo shoppingBo) {

        List<ShoppingBo> list = shoppingService.getShoppingList(shoppingBo);
        return list;

    }

    @PostMapping("/save")
    @ApiOperation(value = "新增购物清单", notes = "新增购物清单")
    public Integer addShopping(ShoppingBo shoppingBo) {
        return shoppingService.addShopping(shoppingBo);
    }

    @PostMapping("/send/mq")
    @ApiOperation(value = "发送mq消息", notes = "发送mq消息")
    public String sendMQ(ShoppingBo shoppingBo) {
        return shoppingService.sendMQ(shoppingBo);
    }


}
