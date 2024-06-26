package org.example.myrpcframework.rpcFrameworkSimple.remoting.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.exception.RpcException;
import org.example.myrpcframework.rpcFrameworkCommon.factory.SingletonFactory;
import org.example.myrpcframework.rpcFrameworkSimple.provider.Impl.ZkServiceProviderImpl;
import org.example.myrpcframework.rpcFrameworkSimple.provider.ServiceProvider;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    //获取serviceProvider
    public RpcRequestHandler(){
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    //handle 方法是处理RPC请求的入口。它首先通过 serviceProvider 获取服务实例，
    //然后调用 invokeTargetMethod 方法来执行具体的远程方法调用。
    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    //invokeTargetMethod 方法使用Java反射API来动态调用服务对象的方法。
    //它首先获取方法对象，然后使用该方法的 invoke 方法执行调用，并将结果返回。
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;
        try{
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }catch(NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e){
            throw new RpcException(e.getMessage(), e);
        }

        return result;
    }

}
