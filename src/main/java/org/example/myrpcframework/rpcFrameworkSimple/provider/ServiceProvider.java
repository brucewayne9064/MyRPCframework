package org.example.myrpcframework.rpcFrameworkSimple.provider;


import org.example.myrpcframework.rpcFrameworkSimple.config.RpcServiceConfig;

public interface ServiceProvider {

    //addService 方法是服务管理的内部逻辑，负责在本地注册服务；
    void addService(RpcServiceConfig rpcServiceConfig);

    Object getService(String rpcServiceName);

    //将 RPC 服务发布到服务注册中心,使其可以被客户端或其他微服务发现和访问。
    void publishService(RpcServiceConfig rpcServiceConfig);
}
