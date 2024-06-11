package org.example.myrpcframework.rpcFrameworkSimple.registry.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.example.myrpcframework.rpcFrameworkSimple.registry.ServiceRegistry;
import org.example.myrpcframework.rpcFrameworkSimple.registry.zk.util.CuratorUtil;

import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.
    }
}
