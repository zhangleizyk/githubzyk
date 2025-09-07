package com.zyk.consumer.service;

import com.zyk.consumer.bo.ShoppingBo;

import java.util.List;

public interface ShoppingService {

    List<ShoppingBo> getShoppingList(ShoppingBo shoppingBo);

    Integer addShopping(ShoppingBo shoppingBo);

    String sendMQ(ShoppingBo shoppingBo);
}
