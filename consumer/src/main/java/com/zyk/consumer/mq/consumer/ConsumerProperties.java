package com.zyk.consumer.mq.consumer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Data
@Component
@ConfigurationProperties(prefix = "rocketmq.consumer")
//@DynamicProps
public class ConsumerProperties {
    /** Endpoints rocketMQ端点 */
    private String endpoints;

    /** 组名 */
    private String group;

    /** 主题 */
    private List<String> topicList;

    private int consumeThreadCount = 8;

    /**
     * 消费组 数
     */
    private int consumeGroupCount = 100;
}
