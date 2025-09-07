//package com.zyk.consumer.mq.consumer;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.collection.CollectionUtil;
//import com.alibaba.fastjson.JSON;
//import com.neusoft.rs.common.cache.GlobalCache;
//import com.neusoft.rs.common.clock.SystemClock;
//import com.neusoft.rs.common.constant.EdaConstant;
//import com.neusoft.rs.common.enums.ErrorCodeEnum;
//import com.neusoft.rs.common.enums.ReviewStateEnum;
//import com.neusoft.rs.common.enums.ValidStateEnum;
//import com.neusoft.rs.common.exception.EdaBusinessException;
//import com.neusoft.rs.common.util.MessagePushUtil;
//import com.neusoft.rs.common.util.RedisUtil;
//import com.neusoft.rs.config.properties.TargetProperties;
//import com.neusoft.rs.module.event.service.business.PushService;
//import com.neusoft.rs.module.event.service.business.ResendTaskInfoService;
//import com.neusoft.rs.module.manage.bo.TargetBo;
//import com.neusoft.rs.module.manage.dao.EventTargetDao;
//import com.neusoft.rs.module.manage.dto.ConsumerGroupStateDto;
//import com.neusoft.rs.module.manage.service.EventUniqueInstanceIdService;
//import com.neusoft.rs.module.trace.service.TraceBatchService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.apis.ClientConfiguration;
//import org.apache.rocketmq.client.apis.ClientException;
//import org.apache.rocketmq.client.apis.ClientServiceProvider;
//import org.apache.rocketmq.client.apis.consumer.FilterExpression;
//import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
//import org.apache.rocketmq.client.apis.consumer.PushConsumer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.*;
//import java.util.stream.Collectors;
//
//import static java.util.concurrent.TimeUnit.SECONDS;
//
//@Configuration
//@ConditionalOnProperty(value = "rocketmq.consumer.enabled", havingValue = "true")
//@Slf4j
//public class ConsumerConfig {
//
//
//    @Resource
//    EventTargetDao eventTargetDao;
//
//    private Map<String, List<PushConsumer>> consumerMap;
//
//    @Resource
//    private PushService pushService;
//
//    @Resource
//    private TargetProperties targetProperties;
//
//    @Resource
//    private RedisUtil redisUtil;
//
//    @Resource
//    private TraceBatchService traceBatchService;
//
//    @Resource
//    private ResendTaskInfoService resendTaskInfoService;
//
//    @Resource
//    private EventUniqueInstanceIdService eventUniqueInstanceIdService;
//
//    @Resource
//    private MessagePushUtil messagePushUtil;
//
//    /**
//     * 飞书推送的环境：1-开发，2-测试，3-生产
//     */
//    @Value("${eventbus.event.messageEnvironment}")
//    private String messageEnvironment;
//
//    /**
//     * 事件消费重试飞书推送状态开关 1：开启，0：关闭
//     */
//    @Value("${eventbus.event.mq.retryMessageStatus}")
//    private String retryMessageStatus;
//
//    //    @Bean
//    public void builderPushConsumer() {
//        // TODO 本项目消费方式使用PushConsumer消费，如需要SimpleConsumer消费机制请参照官网示例
//
//        log.info("==============》 开始初始化消费者 《===================");
//
//        // TODO 每个主题对应一种消息类型，所以初始化消费多个主题以达到消费多种类型消息。本项目只对接了普通消息和定时/延时消息。
//        // 其余消息可参照官网：https://rocketmq.apache.org/zh/docs/domainModel/04message
//
//        try {
//
//            final ClientServiceProvider provider = ClientServiceProvider.loadService();
//
//            // 连接信息构建
//            ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
//                    // 端点
//                    .setEndpoints(consumerProperties.getEndpoints())
//                    .build();
//            consumerMap = new HashMap<>();
//
//            for (String consumerGroupKey : GlobalCache.getConsumerGroupTargetMap().keySet()) {
////                List<TargetBo> consumerTargets = GlobalCache.getConsumerGroupTargetMap().get(consumerGroupKey);
////                StringBuffer tagBuffer = new StringBuffer();
////                for (TargetBo consumerTarget : consumerTargets) {
////                    tagBuffer.append(consumerTarget.getConsumerGroup()).append("||");
////                }
////                String tagStr = tagBuffer.substring(0, tagBuffer.toString().length() - 2);
//
//                Map<String, FilterExpression> topicMap = new HashMap<>();
//                for (String topic : consumerProperties.getTopicList()) {
//                    // 订阅消息的过滤规则，表示订阅所有Tag的消息。
//                    FilterExpression filterExpression = new FilterExpression(consumerGroupKey, FilterExpressionType.TAG);
//                    topicMap.put(topic, filterExpression);
//                }
//                // 消费者构建
//                this.builderPushConsumer(consumerGroupKey, provider, clientConfiguration, topicMap);
//            }
//            //10/19 - yue.zhang-zhang
//            //写入 redis 通道状态
////            this.writeConsumerGroupState();
//
//        } catch (ClientException e) {
//
//            log.error("==============》 初始化消费者失败：{}", e.getMessage());
//        }
//    }
//
//    /**
//     * 构建消费者
//     *
//     * @param consumerGroupKey
//     * @param provider
//     * @param clientConfiguration
//     * @param topicMap
//     */
//    private void builderPushConsumer(String consumerGroupKey, ClientServiceProvider provider, ClientConfiguration clientConfiguration, Map<String, FilterExpression> topicMap) throws ClientException {
//        //消息组名
//        String consumerGroup = EdaConstant.CONSUMER_GROUP_PREFIX + consumerGroupKey;
//        for (int i = 0; i < consumerProperties.getConsumeThreadCount(); i++) {
//            // 初始化PushConsumer，需要绑定消费者分组ConsumerGroup、通信参数以及订阅关系。
//            PushConsumer pushConsumer = provider.newPushConsumerBuilder()
//                    //
//                    .setClientConfiguration(clientConfiguration)
//                    // 设置消费者分组。
//                    .setConsumerGroup(consumerGroup)
//                    // 设置预绑定的订阅关系。
//                    .setSubscriptionExpressions(topicMap)
//                    // 设置消费监听器。
////                        .setMessageListener(new RocketMQListener(targetBo))
//                    .setMessageListener(new RocketMQListener(pushService, targetProperties, redisUtil, traceBatchService, resendTaskInfoService, consumerGroupKey, messagePushUtil, messageEnvironment, retryMessageStatus))
//                    // 设置消费并行线程数
//                    .setConsumptionThreadCount(20)
//                    .build();
//            //存入MAP中
//            this.pubConsumterMap(pushConsumer);
//
//            log.info("==============》 初始化消费者:消费组：{},成功：{}", consumerGroup, pushConsumer);
//
//        }
//    }
//
//    /**
//     * 写入 redis 通道状态
//     */
//    private void writeConsumerGroupState() {
//        List<ConsumerGroupStateDto> consumerGroupStateDtos = new ArrayList<>();
//        for (String consumerGroup : consumerMap.keySet()) {
//            ConsumerGroupStateDto consumerGroupStateDto = new ConsumerGroupStateDto();
//            consumerGroupStateDto.setConsumrerGroupId(consumerGroup);
//            consumerGroupStateDto.setState(EdaConstant.EDA_CHANNEL_STATE_ON);
//            consumerGroupStateDtos.add(consumerGroupStateDto);
//        }
//        //写入 redis 通道状态
//        //项目已有 redistemplate 只支持 string 型，避免修改 redistemplate 定义。放入前将对象先转换成字符串
//        String strValue = JSON.toJSONString(consumerGroupStateDtos);
//        boolean setResult = redisUtil.set(EdaConstant.REDIS_EDA_CHANNEL_PREFIX + eventUniqueInstanceIdService.generateInstanceId(), strValue, EdaConstant.REDIS_EDA_CHANNEL_EXPIRE);
//        if (setResult) {
//            log.info("写入实例:{}通道状态成功", EdaConstant.REDIS_EDA_CHANNEL_PREFIX + eventUniqueInstanceIdService.generateInstanceId());
//        } else {
//            log.info("写入实例:{}通道状态失败", EdaConstant.REDIS_EDA_CHANNEL_PREFIX + eventUniqueInstanceIdService.generateInstanceId());
//        }
//    }
//
//    /**
//     * 记录消费组创建的消费组实例对象
//     *
//     * @param pushConsumer
//     */
//    private void pubConsumterMap(PushConsumer pushConsumer) {
//        if (consumerMap == null) {
//            consumerMap = new HashMap<>();
//        }
//        String consumerGroup = pushConsumer.getConsumerGroup();
//        List<PushConsumer> pushConsumers = consumerMap.get(consumerGroup);
//        if (pushConsumers == null) {
//            pushConsumers = new ArrayList<>();
//            consumerMap.put(consumerGroup, pushConsumers);
//        }
//        pushConsumers.add(pushConsumer);
//    }
//
//    /**
//     * 监听消费组状态，处理任务
//     * 10/19 - yue.zhang-zhang
//     */
////    @SneakyThrows
////    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
//    public void monitor() {
//        log.info("监听通道任务", SystemClock.nowFormatDate());
//
//        //设置过期时间 30 秒刷新一次
//        redisUtil.expire(EdaConstant.REDIS_EDA_CHANNEL_PREFIX + eventUniqueInstanceIdService.generateInstanceId(), EdaConstant.REDIS_EDA_CHANNEL_EXPIRE);
//        redisUtil.expire(EdaConstant.REDIS_EDA_CHANNEL_TASK_PREFIX + eventUniqueInstanceIdService.generateInstanceId(), EdaConstant.REDIS_EDA_CHANNEL_EXPIRE);
//
//        //获取通道任务
//        Object redisValue = redisUtil.lGetIndex(EdaConstant.REDIS_EDA_CHANNEL_TASK_PREFIX + eventUniqueInstanceIdService.generateInstanceId(), 0);
//        if (redisValue != null) {
//            String taskStrValue = (String) redisValue;
//            ConsumerGroupStateDto task = JSON.parseObject(taskStrValue, ConsumerGroupStateDto.class);
//
//            if (task != null) {
//                String taskGroupId = EdaConstant.CONSUMER_GROUP_PREFIX + task.getConsumrerGroupId();
//                if (EdaConstant.EDA_CHANNEL_STATE_OFF.equals(task.getState())) {
//                    //获取通道状态
//                    String channelStateStrValue = (String) redisUtil.get(EdaConstant.REDIS_EDA_CHANNEL_PREFIX + eventUniqueInstanceIdService.generateInstanceId());
//                    List<ConsumerGroupStateDto> consumerGroupStateDtos = JSON.parseArray(channelStateStrValue, ConsumerGroupStateDto.class);
//                    if (consumerGroupStateDtos != null) {
//                        for (ConsumerGroupStateDto consumerGroupState : consumerGroupStateDtos) {
//
//                            if ((taskGroupId).equals(consumerGroupState.getConsumrerGroupId())) {
//                                shutdownPushConsumer(taskGroupId);
//                            }
//                        }
//
//                    }
//                } else {
//                    //开启通道
//                    log.info("打开通道：{}", taskGroupId);
//                    builderPushConsumer(task.getConsumrerGroupId());
//                    log.info("向内存持久化通道状态：{}", taskGroupId);
//
//                }
//                //任务执行完毕，移除任务
//                redisUtil.lRemove(EdaConstant.REDIS_EDA_CHANNEL_TASK_PREFIX + eventUniqueInstanceIdService.generateInstanceId(), 0, taskStrValue);
//                //重新给实例 channel 状态赋值
//                this.writeConsumerGroupState();
//
//            }
//
//        }
//
//    }
//
//    public void shutdownPushConsumer(String taskGroupId) {
//        long s = System.currentTimeMillis();
//
//        //消息组名
//        String consumerGroup = EdaConstant.CONSUMER_GROUP_PREFIX + taskGroupId;
//        //关闭通道
//        List<PushConsumer> pushConsumers = consumerMap.get(consumerGroup);
//        if (CollUtil.isEmpty(pushConsumers)) {
//            log.warn("通道不存在：{}", taskGroupId);
//            return;
//        }
//        //创建线程池，并行调用close
//        // 创建线程池
//        ExecutorService executorService = new ThreadPoolExecutor(
//                EdaConstant.CORE_POOL_SIZE,
//                EdaConstant.MAX_POOL_SIZE,
//                EdaConstant.KEEP_ALIVE_TIME,
//                SECONDS,
//                new LinkedBlockingDeque<>(EdaConstant.MAP_INIT_SIZE),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.DiscardOldestPolicy());
//
//        // 并行调用close
//        for (PushConsumer pushConsumer : pushConsumers) {
//            executorService.submit(() -> {
//                try {
//                    long start = System.currentTimeMillis();
//                    pushConsumer.close();
//                    long end = System.currentTimeMillis();
//                    log.info("关闭通道：{}，耗时：{}", taskGroupId, end - start);
//                } catch (IOException e) {
//                    log.error("==============》 关闭通道，关闭消费者失败：{}", e.getMessage());
//                    throw new EdaBusinessException(ErrorCodeEnum.DE_ACTIVE_CONSUMER_GROUP_ERROR.getCode());
//                }
//            });
//        }
//        // 关闭线程池
//        executorService.shutdown();
//
//        try {
//            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
//                executorService.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            log.error("线程池关闭异常-shutdownPushConsumer，原因：{}", e.getMessage());
//            executorService.shutdownNow();
//            Thread.currentThread().interrupt();
//            log.error("通道下线失败, InterruptedException!");
//            throw new EdaBusinessException(ErrorCodeEnum.DE_ACTIVE_CONSUMER_GROUP_ERROR.getCode());
//        }
//
//        log.info("关闭通道：{}", taskGroupId);
//        //移除 map 中的 通道
//        consumerMap.remove(taskGroupId);
//        log.info("移除内存持久化通道状态：{}", taskGroupId);
//        long e = System.currentTimeMillis();
//        log.info("关闭通道：{}，总耗时：{}", taskGroupId, e - s);
//
////        //消息组名
////        String consumerGroup = EdaConstant.CONSUMER_GROUP_PREFIX + taskGroupId;
////        //关闭通道
////        List<PushConsumer> pushConsumers = consumerMap.get(consumerGroup);
////        if (pushConsumers != null) {
////            long s = System.currentTimeMillis();
////            for (PushConsumer pushConsumer : pushConsumers) {
////                try {
////                    long start = System.currentTimeMillis();
////                    pushConsumer.close();
////                    long end = System.currentTimeMillis();
////                    log.info("关闭通道：{}，耗时：{}", taskGroupId, end - start);
////                } catch (IOException e) {
////                    log.error("==============》 执行任务，初始化消费者失败：{}", e.getMessage());
////                    throw new EdaBusinessException(ErrorCodeEnum.DE_ACTIVE_CONSUMER_GROUP_ERROR.getCode());
////                }
////            }
////            long e = System.currentTimeMillis();
////            log.info("关闭通道：{}，总耗时：{}", taskGroupId, e - s);
////        }
////        log.info("关闭通道：{}", taskGroupId);
////        //移除 map 中的 通道
////        consumerMap.remove(taskGroupId);
////        log.info("移除内存持久化通道状态：{}", taskGroupId);
//    }
//
//    /**
//     * 创建消费者
//     *
//     * @param consumrerGroupId
//     */
//    public void builderPushConsumer(String consumrerGroupId) {
//        final ClientServiceProvider provider = ClientServiceProvider.loadService();
//        // 连接信息构建
//        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
//                // 端点
//                .setEndpoints(consumerProperties.getEndpoints())
//                .build();
//        List<TargetBo> targetBoList = eventTargetDao.getAllTargetJoinConsumerGrpup(ValidStateEnum.VALID_STATE_1.getCode(),
//                ReviewStateEnum.REVIEW_STATE_1.getCode(), null, null);
//        if (CollectionUtil.isEmpty(targetBoList)) {
//            log.warn("未查询到上线状态的事件目标");
//            return;
//
//        }
//        List<TargetBo> consumerTargets = targetBoList.stream().filter(targetBo -> targetBo.getConsumerGroup().equals(consumrerGroupId)).collect(Collectors.toList());
//
////        StringBuffer tagBuffer = new StringBuffer();
////        for (TargetBo consumerTarget : consumerTargets) {
////            tagBuffer.append(consumerTarget.getTargetId()).append("||");
////        }
////        String tagStr = null;
////        if (tagBuffer.length() != 0) {
////            tagStr = tagBuffer.substring(0, tagBuffer.toString().length() - 2);
////        } else {
////            //没有查询可用目标，虽然 消费者创建了 ，但是设置filter NO_FILTER
////            tagStr = "NO_FILTER";
////        }
//
//        Map<String, FilterExpression> topicMap = new HashMap<>();
//        for (String topic : consumerProperties.getTopicList()) {
//            // 订阅消息的过滤规则，表示订阅所有Tag的消息。
////            FilterExpression filterExpression = new FilterExpression(tagStr, FilterExpressionType.TAG);
//            FilterExpression filterExpression = new FilterExpression(consumrerGroupId, FilterExpressionType.TAG);
//
//            topicMap.put(topic, filterExpression);
//        }
//        try {
//            this.builderPushConsumer(consumrerGroupId, provider, clientConfiguration, topicMap);
//        } catch (ClientException e) {
//            log.error("==============》 执行任务，初始化消费者失败：{}", e.getMessage());
//            throw new EdaBusinessException(ErrorCodeEnum.ACTIVE_CONSUMER_GROUP_ERROR.getCode());
//        }
//    }
//
//}
