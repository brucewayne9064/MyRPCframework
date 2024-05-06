package org.example.myrpcframework.rpcFrameworkSimple.registry.zk;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.example.myrpcframework.rpcFrameworkSimple.registry.ServiceDiscovery;
import org.example.myrpcframework.rpcFrameworkSimple.registry.zk.util.CuratorUtil;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    public ZkServiceDiscoveryImpl() {

    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        //连接zookeeper服务器
        CuratorFramework zkClient = CuratorUtil.getZkClient();

    }
}
