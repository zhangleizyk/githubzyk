//package com.zyk.consumer.mq.consumer;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.neusoft.rs.common.cache.GlobalCache;
//import com.neusoft.rs.common.clock.SystemClock;
//import com.neusoft.rs.common.constant.EdaConstant;
//import com.neusoft.rs.common.enums.PushEventResEnum;
//import com.neusoft.rs.common.enums.TraceStatusEnum;
//import com.neusoft.rs.common.enums.TraceTypeEnum;
//import com.neusoft.rs.common.util.IdUtil;
//import com.neusoft.rs.common.util.MessagePushUtil;
//import com.neusoft.rs.common.util.RedisKeyUtil;
//import com.neusoft.rs.common.util.RedisUtil;
//import com.neusoft.rs.config.properties.TargetProperties;
//import com.neusoft.rs.module.event.bo.EventBodyBo;
//import com.neusoft.rs.module.event.bo.EventDetailBo;
//import com.neusoft.rs.module.event.service.business.PushService;
//import com.neusoft.rs.module.event.service.business.ResendTaskInfoService;
//import com.neusoft.rs.module.manage.bo.TargetBo;
//import com.neusoft.rs.module.trace.bo.TraceBo;
//import com.neusoft.rs.module.trace.service.TraceBatchService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
//import org.apache.rocketmq.client.apis.consumer.MessageListener;
//import org.apache.rocketmq.client.apis.message.MessageView;
//import org.jetbrains.annotations.NotNull;
//
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author yue.zhang-zhang
// **/
////@Component
//@Slf4j
//public class RocketMQListener implements MessageListener {
//
////    public RocketMQListener(TargetBo targetBo) {
////        this.targetBo = targetBo;
////    }
//
//    public RocketMQListener(PushService pushService, TargetProperties targetProperties,
//                            RedisUtil redisUtil, TraceBatchService traceBatchService,
//                            ResendTaskInfoService resendTaskInfoService, String consumerGroupKey,
//                            MessagePushUtil messagePushUtil, String messageEnvironment, String retryMessageStatus) {
//        this.pushService = pushService;
//        this.targetProperties = targetProperties;
//        this.redisUtil = redisUtil;
//        this.traceBatchService = traceBatchService;
//        this.resendTaskInfoService = resendTaskInfoService;
//        this.consumerGroupKey = consumerGroupKey;
//        this.messagePushUtil = messagePushUtil;
//        this.messageEnvironment = messageEnvironment;
//        this.retryMessageStatus = retryMessageStatus;
//    }
//
//    private PushService pushService;
//
//    private TargetProperties targetProperties;
//
//    private RedisUtil redisUtil;
//
//    private TraceBatchService traceBatchService;
//
//    private ResendTaskInfoService resendTaskInfoService;
//
//    private MessagePushUtil messagePushUtil;
//
//    /**
//     * consumerGroupKey
//     */
//    private String consumerGroupKey;
//
//    /**
//     * 飞书推送的环境：1-开发，2-测试，3-生产
//     */
//    private String messageEnvironment;
//
//    /**
//     * 事件消费重试飞书推送状态开关 1：开启，0：关闭
//     */
//    private String retryMessageStatus;
//
//    /**
//     * 消息消费 返回success表示消费成功
//     *
//     * @param messageView
//     * @return org.apache.rocketmq.client.apis.consumer.ConsumeResult
//     **/
//    @Override
//    public ConsumeResult consume(MessageView messageView) {
//        log.info("监听到消息：{}", messageView.toString());
//
//        String messageId = messageView.getMessageId().toString();
//        int deliveryAttempt = messageView.getDeliveryAttempt();
//
//        // 重试次数大于设定次数+1时，推送飞书
//        if (deliveryAttempt > Integer.parseInt(targetProperties.getRetryTimes()) + 1) {
//            try {
//                log.info("【事件消费重试】,messageId:{},deliveryAttempt:{}", messageId, deliveryAttempt);
//                // 推送开关
//                if (EdaConstant.CONSTANT_STRING_1.equals(retryMessageStatus)) {
//                    // 指定字符集（Charset）
//                    Charset charset = StandardCharsets.UTF_8;
//                    // 将ByteBuffer转换为String
//                    String message = charset.decode(messageView.getBody()).toString();
//                    EventBodyBo<Object> eventBodyBo = JSON.parseObject(message, EventBodyBo.class);
//
//                    // 发送飞书消息
//                    String title = "事件消费重试";
//                    StringBuilder newTitle = new StringBuilder();
//                    newTitle.append(title);
//                    // 推送环境
//                    if (EdaConstant.CONSTANT_STRING_0.equals(messageEnvironment)) {
//                        newTitle.append("(本地环境)");
//                    } else if (EdaConstant.CONSTANT_STRING_1.equals(messageEnvironment)) {
//                        newTitle.append("(开发环境)");
//                    } else if (EdaConstant.CONSTANT_STRING_2.equals(messageEnvironment)) {
//                        newTitle.append("(测试环境)");
//                    } else if (EdaConstant.CONSTANT_STRING_3.equals(messageEnvironment)) {
//                        newTitle.append("(生产环境)");
//                    }
//
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("【事件名称】：" + eventBodyBo.getEventName()).append("\n");
//                    sb.append("【轨迹ID】：" + eventBodyBo.getTargetTraceId()).append("\n");
//                    sb.append("【EntityId】：" + eventBodyBo.getEntityId()).append("\n");
//                    sb.append("【下游URL】：" + eventBodyBo.getTargetFullUrl()).append("\n");
//                    sb.append("【MessageId】：" + messageId).append("\n");
//                    sb.append("【重试次数】：" + deliveryAttempt).append("\n");
//                    sb.append("【推送时间】：" + SystemClock.nowFormatDate(EdaConstant.DATETIME_FORMAT_MS));
//                    // 固定推送人员
//                    String recivers = "yue.zhang-zhang,zhlei_zhang";
//                    messagePushUtil.sendFeiShuMessageCommon(newTitle.toString(), sb.toString(), recivers);
//                    return ConsumeResult.SUCCESS;
//                }
//            } catch (Exception e) {
//                log.error("处理消费重试报异常,MessageId：{},消息内容：{},异常原因：{}", messageId, messageView.toString(), e.getMessage());
//                return ConsumeResult.SUCCESS;
//            }
//        }
//
//        try {
//            // 消费幂等
//            log.info("消费幂等,messageId:{},deliveryAttempt:{}", messageId, deliveryAttempt);
//
//            // 达到重试次数
//            if (deliveryAttempt > Integer.parseInt(targetProperties.getRetryTimes())) {
//                // 组装key
//                String lockKey = "event:msgId:" + messageId + ":" + deliveryAttempt;
//                log.info("消费幂等,组装key：{}", lockKey);
//                // 获取分布式锁
//                boolean bool = redisUtil.acquireLock(lockKey);
//                if (!bool) {
//                    // 记录日志
//                    log.info("已经处理过此message,消息内容：{}", messageView.toString());
//                    return ConsumeResult.SUCCESS;
//                }
//            }
//        } catch (Exception e) {
//            log.error("消费幂等报异常,消息内容：{},异常原因：{}", messageView.toString(), e.getMessage());
//        }
//
//        EventBodyBo<Object> eventBodyBo = null;
//        String eventName = null;
//        JSONObject content = null;
//        String eventStr = null;
//        JSONObject eventObj = null;
//        Integer retryTimes = null;
//        String key = null;
//        EventDetailBo<Object> eventDetailBo = null;
//        String traceGroupId = null;
//        String traceId = null;
//        Date eventOperateTime = null;
//        // traceBo 参数组装
//        TraceBo traceBo = new TraceBo();
//        try {
//            traceId = IdUtil.getSnowflakeId();
//            traceBo.setTraceId(traceId);
//            traceBo.setExecuteStarttime(SystemClock.nowDate());
//            eventDetailBo = new EventDetailBo<>();
//
//            // 指定字符集（Charset）
//            Charset charset = StandardCharsets.UTF_8;
//            // 将ByteBuffer转换为String
//            String message = charset.decode(messageView.getBody()).toString();
//            log.info("监听的消息内容：{}", message);
//            eventBodyBo = JSON.parseObject(message, EventBodyBo.class);
//            eventName = eventBodyBo.getEventName();
//            traceGroupId = eventBodyBo.getTraceGroupId();
//            traceBo.setEventName(eventName);
//            traceBo.setTargetContextPath(eventBodyBo.getTargetFullUrl());
//            traceBo.setTraceGroupId(traceGroupId);
//            traceBo.setEntityId(eventBodyBo.getEntityId());
//            //判断是否为自己负责的推送目标
//
//            content = (JSONObject) eventBodyBo.getContent();
//            eventStr = eventBodyBo.getContent().toString();
//            eventDetailBo.setFilterEvent(eventStr);
//            eventObj = JSON.parseObject(eventStr);
//            retryTimes = messageView.getDeliveryAttempt();
//            traceBo.setRetryTimes(retryTimes);
//
//            // key怎么取
//            String[] keys = new String[1];
//            messageView.getKeys().toArray(keys);
//            key = keys[0];
//            traceBo.setEventKey(key);
//
//            if (content.get(EdaConstant.PROP_OPERATE_TIME) == null || "".equals(content.get(EdaConstant.PROP_OPERATE_TIME).toString())) {
//                eventOperateTime = null;
//            } else {
//                eventOperateTime = new Date(Long.parseLong(content.get(EdaConstant.PROP_OPERATE_TIME).toString()));
//            }
//            traceBo.setOperateTime(eventOperateTime);
//
////            eventDetailBo = new EventDetailBo<>();
//            eventDetailBo.setEventName(eventName);
//            eventDetailBo.setEventBody(message);
//            eventDetailBo.setEventKey(key);
////            eventDetailBo.setEntityId(content.get(EdaConstant.PROP_ENTITY_ID).toString());
//            eventDetailBo.setEntityId(eventBodyBo.getEntityId());
//            eventDetailBo.setOperateTime(eventOperateTime);
//            eventDetailBo.setTargetFullUrl(eventBodyBo.getTargetFullUrl());
//            eventDetailBo.setEventStr(eventStr);
//            eventDetailBo.setEventObj(eventObj);
//
//            // 获取trace_group_id
////            traceGroupId = eventBodyBo.getTraceGroupId();
//            log.info("mq5-EDA监听到事件，traceId:{},traceGroupId:{},事件名：{}，事件内容：{}，时间：[{}]",
//                    traceId, traceGroupId, eventName, message, SystemClock.nowFormatDate());
//
////            traceBo.setTraceId(eventBodyBo.getTargetTraceId());
//            traceBo.setTraceId(traceId);
//            traceBo.setEventName(eventName);
//            traceBo.setEventKey(key);
//            traceBo.setExecuteStarttime(SystemClock.nowDate());
//            traceBo.setTargetContextPath(eventBodyBo.getTargetFullUrl());
//            traceBo.setTraceGroupId(traceGroupId);
//            traceBo.setTraceKey(eventBodyBo.getTraceKey());
//            traceBo.setEntityId(eventBodyBo.getEntityId());
//            traceBo.setOperateTime(eventOperateTime);
//            traceBo.setRetryTimes(retryTimes);
//        } catch (Exception e) {
//            // 事件解析失败
//            String msg = "事件解析失败,异常原因：" + e.getMessage();
//            log.error(msg);
//            e.printStackTrace();
//
//            traceBo.setExecuteEndtime(SystemClock.nowDate());
//            traceBo.setTimeConsuming(0);
//            traceBo.setErrorMsg(msg);
//            traceBo.setActionType(TraceTypeEnum.TRACE_TYPE_PUSH.getCode());
//            traceBo.setSendStatus(TraceStatusEnum.TRACE_STATUS_PUSH_FAIL.getCode());
//
//            //todo ServiceName restProtocol filterPattern filterEvent
//            //todo 没过滤
//            eventDetailBo.setFilterEvent(eventStr);
////            traceBo.setServiceName();
//            log.info("事件解析失败，直接进入blockHandle，traceId:{},traceGroupId:{}", traceId, traceGroupId);
//            return blockHandle(eventDetailBo, traceBo);
//
//        }
//
//        //  处理业务
//        PushEventResEnum pushEventResEnum = null;
//
//        try {
//            pushEventResEnum = pushService.pushEvent(eventDetailBo, traceBo);
//        } catch (Exception e) {
//            log.error("推送流程报异常,异常原因：{}", e.getMessage());
//            e.printStackTrace();
//            String msg="推送流程报异常推送流程报异常,异常原因：{}"+ e.getMessage();
//            traceBo.setErrorMsg(msg);
//            return failHandle(messageView, eventDetailBo, traceBo);
//        }
//        //成功
//        if (PushEventResEnum.PUSH_EVENT_RES_SUCCESS.getCode().equals(pushEventResEnum.getCode())) {
//            return successHandle(eventName, eventBodyBo, traceBo);
//        }
//        //失败
//        else if (PushEventResEnum.PUSH_EVENT_RES_FAIL.getCode().equals(pushEventResEnum.getCode())) {
//            return failHandle(messageView, eventDetailBo, traceBo);
//        }
//        //阻塞
//        else if (PushEventResEnum.PUSH_EVENT_RES_BLOCK.getCode().equals(pushEventResEnum.getCode())) {
//            return blockHandle(eventDetailBo, traceBo);
//        }
//        //过滤
//        else if (PushEventResEnum.PUSH_EVENT_RES_FILTER.getCode().equals(pushEventResEnum.getCode())) {
//            return filterHandle(eventDetailBo, traceBo);
//        }
//
//        //todo trace 推送成功
//
//        // 没报错消费成功返回成功
//        return ConsumeResult.SUCCESS;
//
//    }
//
//    /**
//     * 是否为自己负责的消息
//     *
//     * @param eventBodyBo
//     * @return
//     */
//    private boolean isDuty(EventBodyBo<Object> eventBodyBo) {
//        boolean bol = false;
//
//        //说明是自己负责的
//        if (StringUtils.isNotEmpty(eventBodyBo.getTargetFullUrl())) {
//            List<TargetBo> consumerTargets = GlobalCache.getConsumerGroupTargetMap().get(consumerGroupKey);
//            for (TargetBo consumerTarget : consumerTargets) {
//                if (consumerTarget.getFullUrl().equals(eventBodyBo.getTargetFullUrl())) {
//                    bol = true;
//                    break;
//                }
//            }
//
//        }
//
//        return bol;
//    }
//
//    @NotNull
//    private ConsumeResult filterHandle(EventDetailBo<Object> eventDetailBo, TraceBo traceBo) {
//        //redis-cnt--
//        String cntRedisKey = RedisKeyUtil.getCntRedisKey(eventDetailBo.getEventName(), eventDetailBo.getTargetFullUrl(), eventDetailBo.getEntityId());
//        long a = redisUtil.decr(cntRedisKey, 1);
//
//        //trace
//        traceBatchService.pushEventFilter(traceBo);
//        return ConsumeResult.SUCCESS;
//    }
//
//    @NotNull
//    private ConsumeResult successHandle(String eventName, EventBodyBo<Object> eventBodyBo, TraceBo traceBo) {
//        //redis-cnt--
//        String cntRedisKey = RedisKeyUtil.getCntRedisKey(eventName, eventBodyBo.getTargetFullUrl(), eventBodyBo.getEntityId());
//        redisUtil.decr(cntRedisKey, 1);
//
//        // trace 推送成功
//        traceBatchService.pushEventSuccess(traceBo);
//        return ConsumeResult.SUCCESS;
//    }
//
//    private ConsumeResult blockHandle(EventDetailBo<Object> eventDetailBo, TraceBo traceBo) {
//        //  放入Oracle任务表
//        log.info("放入Oracle,traceId:{},traceGroupId:{},事件名:{},url：{}，entityId：{}",
//                traceBo.getTraceId(), traceBo.getTraceGroupId(), eventDetailBo.getEventName(), eventDetailBo.getTargetFullUrl());
//        resendTaskInfoService.add(traceBo, eventDetailBo.getFilterEvent());
//
//        //redis-cnt--
//        String cntRedisKey = RedisKeyUtil.getCntRedisKey(eventDetailBo.getEventName(), eventDetailBo.getTargetFullUrl(), eventDetailBo.getEntityId());
//        long a = redisUtil.decr(cntRedisKey, 1);
//        //redis-err++
//        String errRedisKey = RedisKeyUtil.getErrRedisKey(eventDetailBo.getEventName(), eventDetailBo.getTargetFullUrl(), eventDetailBo.getEntityId());
//        long b = redisUtil.incr(errRedisKey, 1);
//
//        log.info("cntRedisKey:{}->{},errRedisKey:{}->{}", cntRedisKey, a, errRedisKey, b);
//
//        // trace 推送失败
//        traceBatchService.pushEventFail(traceBo);
//        // 发送飞书消息
//        String title = "事件推送失败";
//        // 组装消息
//        StringBuilder sb = new StringBuilder();
//        sb.append("【事件名称】：" + traceBo.getEventName()).append("\n");
//        sb.append("【事件类型】：推送").append("\n");
//        sb.append("【轨迹ID】：" + traceBo.getTraceId()).append("\n");
//        sb.append("【EntityId】：" + traceBo.getEntityId()).append("\n");
//        sb.append("【下游URL】：" + traceBo.getTargetContextPath()).append("\n");
//        sb.append("【失败信息】：" + traceBo.getErrorMsg()).append("\n");
//        sb.append("【推送时间】：" + SystemClock.nowFormatDate(EdaConstant.DATETIME_FORMAT_MS));
//        log.info("【事件推送失败】推送飞书, 消息内容：{}", sb.toString());
//        messagePushUtil.sendFeiShuMessage(title, sb.toString(), traceBo.getServiceName(), traceBo.getTargetContextPath());
//        return ConsumeResult.SUCCESS;
//    }
//
//    @NotNull
//    private ConsumeResult failHandle(MessageView messageView, EventDetailBo<Object> eventDetailBo, TraceBo traceBo) {
//        //达到重试次数，放入Oracle任务表，redis-cnt--;redis-err++，消息状态置为成功
//        log.info("MessageId:{},DeliveryAttempt:{},RetryTimes:{}",messageView.getMessageId().toString(), messageView.getDeliveryAttempt(), targetProperties.getRetryTimes());
//        if (messageView.getDeliveryAttempt() > Integer.parseInt(targetProperties.getRetryTimes())) {
//            return blockHandle(eventDetailBo, traceBo);
//        }
//        //没达到重试次数，消息状态置为失败，mq发起下一次重试
//        else {
//            //todo trace 推送失败
//            traceBatchService.pushEventFail(traceBo);
//            return ConsumeResult.FAILURE;
//        }
//    }
//}
