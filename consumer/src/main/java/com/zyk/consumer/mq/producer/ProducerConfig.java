package com.zyk.consumer.mq.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Configuration
@Slf4j
public class ProducerConfig {

    /**
     * 远程调用连接信息
     */
    public static Producer producer;
    public static String endpoints = "127.0.0.1:9876";

    @Bean
    public Producer builderProducer() {

        log.info("==============》 开始初始化生产者 《===================");

        try {

            final ClientServiceProvider provider = ClientServiceProvider.loadService();

            // 构建连接信息
            ClientConfiguration configuration = ClientConfiguration.newBuilder()
                    // 端点信息
                    .setEndpoints(endpoints)
                    // 构建
                    .build();

            // 初始化Producer时需要设置通信配置以及预绑定的Topic。
            producer = provider.newProducerBuilder()
                    // TODO 生产者主题预绑定多个，可发送不同主题消息以及不同消息类型消息
                    // 主题
                    .setTopics("topic1")
                    // 连接信息
                    .setClientConfiguration(configuration)
                    // 最大重试次数
                    .setMaxAttempts(3)
                    .build();

            log.info("==============》 初始化生产者成功：{} ", producer);

        } catch (ClientException e) {

            log.info("==============》 初始化生产者失败：{} ", e.getMessage());
            e.printStackTrace();
        }

        return producer;
    }
}
