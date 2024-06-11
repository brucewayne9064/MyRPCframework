package org.example.myrpcframework.rpcFrameworkSimple.provider.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkSimple.config.RpcServiceConfig;
import org.example.myrpcframework.rpcFrameworkSimple.provider.ServiceProvider;
import org.example.myrpcframework.rpcFrameworkSimple.registry.ServiceRegistry;


import java.util.Map;
import java.util.Set;


@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {

    private final Map<String, Object> serviceMap;  // serviceName ， service Object
    private final Set<String> registeredService;
    private final ServiceRegistry


    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {

    }

    @Override
    public Object getService(String rpcServiceName) {
        return null;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {

    }
}
