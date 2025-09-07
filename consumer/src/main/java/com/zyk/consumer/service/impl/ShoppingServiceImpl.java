package com.zyk.consumer.service.impl;

import com.zyk.consumer.bo.ShoppingBo;
import com.zyk.consumer.dao.ShoppingDao;
import com.zyk.consumer.mq.producer.MessageProducer;
import com.zyk.consumer.service.ShoppingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ShoppingServiceImpl implements ShoppingService {

    @Resource
    private ShoppingDao shoppingDao;
    @Resource
    private MessageProducer messageProducer;

    @Override
    public List<ShoppingBo> getShoppingList(ShoppingBo shoppingBo) {
        return shoppingDao.getShoppingList(shoppingBo);
    }

    @Override
    public Integer addShopping(ShoppingBo shoppingBo) {
        return shoppingDao.addShopping(shoppingBo);
    }

    @Override
    public String sendMQ(ShoppingBo shoppingBo) {
        String msg = "小土豆";
        try {
            messageProducer.sendMessage("topic1","tg", msg, "11", "22");
        } catch (ClientException e) {
            log.error("处理失败");
        }
        return "2";
    }
}
