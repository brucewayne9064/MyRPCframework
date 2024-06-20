package org.example.myrpcframework.rpcFrameworkSimple.remoting.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.factory.SingletonFactory;
import org.example.myrpcframework.rpcFrameworkSimple.provider.Impl.ZkServiceProviderImpl;
import org.example.myrpcframework.rpcFrameworkSimple.provider.ServiceProvider;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;


@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    //获取serviceProvider
    public RpcRequestHandler(){
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;
        try{

        }catch(){

        }

        return result;
    }

}
