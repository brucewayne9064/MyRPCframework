package org.example.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Main {
    private static final int BASE_SLEEP_TIME = 1000;  //重试之间等待的初始时间
    private static final int MAX_RETRIES = 3;  //最大重试次数

    public static void main(String[] args) {
        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString("127.0.0.1:2181")  //要连接的服务器列表
                .retryPolicy(retryPolicy)  //重试策略
                .build();

        zkClient.start();

    }
}
