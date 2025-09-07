package com.zyk.consumer.group.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ThreadDemo {
    public static void main(String[] args) {
        ExecutorService service = new ThreadPoolExecutor(6,10,3,SECONDS,
                new LinkedBlockingDeque<>(10), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}
