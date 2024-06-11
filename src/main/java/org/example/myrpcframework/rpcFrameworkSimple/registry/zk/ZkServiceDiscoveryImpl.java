package org.example.myrpcframework.rpcFrameworkSimple.registry.zk;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.example.myrpcframework.rpcFrameworkCommon.enums.LoadBalanceEnums;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcErrorMessageEnums;
import org.example.myrpcframework.rpcFrameworkCommon.exception.RpcException;
import org.example.myrpcframework.rpcFrameworkCommon.extension.ExtensionLoader;
import org.example.myrpcframework.rpcFrameworkCommon.utils.CollectionUtil;
import org.example.myrpcframework.rpcFrameworkSimple.loadbalance.LoadBalance;
import org.example.myrpcframework.rpcFrameworkSimple.registry.ServiceDiscovery;
import org.example.myrpcframework.rpcFrameworkSimple.registry.zk.util.CuratorUtil;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader
                .getExtensionLoader(LoadBalance.class)
                .getExtension(LoadBalanceEnums.LOADBALANCE.getName());
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        //连接zookeeper服务器
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        //得到服务的子节点
        List<String> serviceUrlList = CuratorUtil.getChildrenNodes(zkClient, rpcServiceName);

        if(CollectionUtil.isEmpty(serviceUrlList)){
            throw  new RpcException(RpcErrorMessageEnums.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        //利用一致性哈希负载均衡算法计算出rpcRequest应该使用哪一个zk结点提供的服务
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");  //根据：分割地址
        String host = socketAddressArray[0];  //ip是0号元素
        int port = Integer.parseInt(socketAddressArray[1]);  // 端口号是1号元素
        return new InetSocketAddress(host, port);
    }
}
