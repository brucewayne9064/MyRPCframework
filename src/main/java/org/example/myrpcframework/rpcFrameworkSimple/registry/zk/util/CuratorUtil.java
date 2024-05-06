package org.example.myrpcframework.rpcFrameworkSimple.registry.zk.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcConfigEnums;
import org.example.myrpcframework.rpcFrameworkCommon.utils.PropertiesFileUtil;

import java.util.Properties;

@Slf4j
public class CuratorUtil {

    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private static CuratorFramework zkClient;

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;


    public static CuratorFramework getZkClient(){
        //如果已经启动了，那么直接返回zkclient
        if(zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED){
            return zkClient;
        }
        //如果没有启动，则执行下面的步骤

        //检查用户是否设置了zk地址, 读取配置文件,如果没有就使用默认的zk地址
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnums.RPC_CONFIG_PATH.getPropertyValue());
        String zookeeperAddress = (properties != null) && (properties.getProperty(RpcConfigEnums.ZK_ADDRESS.getPropertyValue()) != null) ?
                properties.getProperty(RpcConfigEnums.ZK_ADDRESS.getPropertyValue()) : DEFAULT_ZOOKEEPER_ADDRESS;

        //设置retry policy
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);



    }

}
