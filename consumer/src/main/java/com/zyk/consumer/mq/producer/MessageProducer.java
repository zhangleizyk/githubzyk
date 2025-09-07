package com.zyk.consumer.mq.producer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yue.zhang-zhang
 * @create 2024/9/5
 **/
@Service
@Slf4j
public class MessageProducer {
    // TODO 注意：RocketMQ5.0在创建Topic时会指定消息类型，默认为普通消息。
    // TODO 注意：RocketMQ5.0不允许推送不同与Topic消息类型的消息到Topic

    // TODO 本项目只对接了普通消息和定时/延时消息
    // 普通消息 ： https://rocketmq.apache.org/zh/docs/featureBehavior/01normalmessage
    // 定时/延时消息：https://rocketmq.apache.org/zh/docs/featureBehavior/02delaymessage
    // 顺序消息：https://rocketmq.apache.org/zh/docs/featureBehavior/03fifomessage
    // 事务消息：https://rocketmq.apache.org/zh/docs/featureBehavior/04transactionmessage

    // TODO 目前只有同步消息，异步消息后续有时间写


    @Resource
    private Producer producer;


    /**
     * 发送普通消息
     *
     * @param topic    主题
     * @param tag      tag
     * @param message
     * @param key
     * @param messageGroup
     * @param <T>
     * @return java.lang.String
     * @author zcy
     */
    public <T> String sendMessage(String topic, String tag, T message, String key, String messageGroup) throws ClientException {

        log.info("发送消息：【主题】：{}, 【tag】：{},【消息key】：{},【消息体】：{},【entityId】：{}",
                topic, tag, key, message, messageGroup);

        // 定时/延时消息发送
        MessageBuilder messageBuilder = new MessageBuilderImpl();
        Message msg = messageBuilder.setTopic(topic)
                // 设置消息索引键，可根据关键字精确查找某条消息。
                .setKeys(key)
                // 设置消息Tag，用于消费端根据指定Tag过滤消息。
                .setTag(tag)
                // 消息体
                .setBody(JSON.toJSONString(message).getBytes())
                .setMessageGroup(messageGroup)
                .build();
//        try {
        // 发送消息，需要关注发送结果，并捕获失败等异常。
        SendReceipt sendReceipt = producer.send(msg);

        log.info("消息发送成功：【消息id】：{}", sendReceipt.getMessageId());
        return sendReceipt.getMessageId().toString();
//        } catch (ClientException e) {
//        log.error("消息发送失败,eventName:{},eventKey:{},原因:{}", tag, key, e.getMessage());
//        }

//        return null;
    }
}
