package org.example.myrpcframework.rpcFrameworkSimple.registry.zk;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
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
                .getExtension();
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
        //
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];  //ip
        int port = Integer.parseInt(socketAddressArray[1]);  // 端口号
        return new InetSocketAddress(host, port);
    }
}
