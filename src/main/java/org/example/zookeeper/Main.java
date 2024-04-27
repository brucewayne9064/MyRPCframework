package org.example.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class Main {
    private static final int BASE_SLEEP_TIME = 1000;  //重试之间等待的初始时间
    private static final int MAX_RETRIES = 3;  //最大重试次数

    public static void main(String[] args) throws Exception {
        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString("127.0.0.1:2181")  //要连接的服务器列表
                .retryPolicy(retryPolicy)  //重试策略
                .build();

        zkClient.start();
//        注意:下面的代码会报错，因为还没创建父节点/node1
//        zkClient.create().forPath("/node1/00001");
//        zkClient.create().withMode(CreateMode.PERSISTENT).forPath("/node1/00002");
//
//        先创建父节点 node1 ，然后再执行上面的代码就不会报错了。
//        zkClient.create().forPath("/node1");
//
//        creatingParentsIfNeeded() 可以保证父节点不存在的时候自动创建父节点，这是非常有用的。
//        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/node1/00001");
//
//        创建临时节点
//        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001");
//
//        创建节点并指定数据内容
//        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00003","java".getBytes());
//        zkClient.getData().forPath("/node1/00003");//获取节点的数据内容，获取到的是 byte数组
//
//        检测节点是否创建成功
//        System.out.println(zkClient.checkExists().forPath("/node1/00001"));//不为null的话，说明节点创建成功

//        删除一个子节点
//        zkClient.delete().forPath("/node1/00001");
//
//        删除一个节点以及其下的所有子节点
//        zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");
//
//        获取/更新节点数据内容
//        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001","java".getBytes());
//        zkClient.getData().forPath("/node1/00001");//获取节点的数据内容
//        zkClient.setData().forPath("/node1/00001","c++".getBytes());//更新节点数据内容

//        获取某个节点的所有子节点路径
//        List<String> childrenPaths = zkClient.getChildren().forPath("/node1");
//        System.out.println(childrenPaths);

//        如何给某个节点注册子节点监听器 。注册了监听器之后，这个节点的子节点发生变化比如增加、减少或者更新的时候，你可以自定义回调操作。
//        String path = "/node1";
//        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
//        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
//            // do something
//        };
//        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
//        pathChildrenCache.start();
//
//        如果你要获取节点事件类型的话，可以通过：
//        pathChildrenCacheEvent.getType();
//
//        一共有下面几种类型：
//        public static enum Type {
//            CHILD_ADDED,//子节点增加
//            CHILD_UPDATED,//子节点更新
//            CHILD_REMOVED,//子节点被删除
//            CONNECTION_SUSPENDED,
//            CONNECTION_RECONNECTED,
//            CONNECTION_LOST,
//            INITIALIZED;
//
//            private Type() {
//            }
//        }
    }
}
