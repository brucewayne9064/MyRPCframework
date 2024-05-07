package org.example.myrpcframework.rpcFrameworkSimple.registry.zk.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcConfigEnums;
import org.example.myrpcframework.rpcFrameworkCommon.utils.PropertiesFileUtil;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtil {

    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private static CuratorFramework zkClient;

    private static final int BASE_SLEEP_TIME = 1000;  // retry的初始睡眠时间
    private static final int MAX_RETRIES = 3;  // 最大retry次数

    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";


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

        //设置retry policy，每次尝试失败，睡眠时间会增加
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();

        zkClient.start();

        try {
            // 检查是否在指定的时间内成功建立连接（30s）
            if(!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)){
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        return zkClient;

    }


    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName){
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    public static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception{
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }




}
