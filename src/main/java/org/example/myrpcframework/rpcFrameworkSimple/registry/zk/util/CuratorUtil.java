package org.example.myrpcframework.rpcFrameworkSimple.registry.zk.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcConfigEnums;
import org.example.myrpcframework.rpcFrameworkCommon.utils.PropertiesFileUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtil {

    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private static CuratorFramework zkClient;

    private static final int BASE_SLEEP_TIME = 1000;  // retry的初始睡眠时间
    private static final int MAX_RETRIES = 3;  // 最大retry次数

    //维护服务名称和其地址的映射关系
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";


    //存放存在的节点路径
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();


    //创建永久节点，不会像临时节点那样消失
    public static void createPersistentNode(CuratorFramework zkClient, String path){
        try{
            if(zkClient.checkExists().forPath(path) != null || REGISTERED_PATH_SET.contains(path)){
                log.info("The node already exists. The node is:[{}]", path);
            }else{
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node has been created. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        }catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try{
                if(p.endsWith(inetSocketAddress.toString())){
                    zkClient.delete().forPath(p);
                }
            }catch(Exception e){
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }

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


    //找到一个节点下的子节点
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


    //Registers to listen for changes to the specified node
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
