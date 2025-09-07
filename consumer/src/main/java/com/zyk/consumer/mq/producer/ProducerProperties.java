package com.zyk.consumer.mq.producer;

//import com.neusoft.rs.config.annotation.DynamicProps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "rocketmq.producer")
//@DynamicProps
public class ProducerProperties {
    /** Endpoints rocketMQ端点 */
    private String endpoints;

    /** 最大重试次数 */
    private int maxAttempts = 3;

    /** 主题 */
    private List<String> topicList;
}
