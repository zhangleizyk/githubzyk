package com.zyk.consumer.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * @author zyk
 */
public class IdUtil {
    private IdUtil() {
    }

    /**
     * 生成雪花算法主键
     *
     * @return 生成的主键
     */
    public static String getSnowflakeId() {
        return IdWorker.getIdStr();
    }

}
