package org.example.myrpcframework.rpcFrameworkSimple.proxy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcErrorMessageEnums;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcResponseCodeEnums;
import org.example.myrpcframework.rpcFrameworkCommon.exception.RpcException;
import org.example.myrpcframework.rpcFrameworkSimple.config.RpcServiceConfig;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcResponse;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.RpcRequestTransport;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.client.NettyRpcClient;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.socket.SocketRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceConfig rpcServiceConfig){
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport){
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = new RpcServiceConfig();
    }

    //当调用代理对象的方法时，实际调用的是InvocationHandler的invoke方法，你可以在invoke方法中定义代理的行为，
    // 比如方法调用的实际逻辑或者额外的处理。
    @SuppressWarnings("unchecked")
    public  <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }



    /**
     * This method is actually called when you use a proxy object to call a method.
     * The proxy object is the object you get through the getProxy method.
     */
    @Override
    @SuppressWarnings("unchecked")
    //指示编译器在该方法或类中抛出异常时，不强制要求调用者处理这些异常。
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args){
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();

        RpcResponse<Object> rpcResponse = null;


//        当 rpc 请求被成功处理（客户端收到服务端的执行结果）之后，我们调用了 unprocessedRequests.complete(rpcResponse) 方法，
//        这样的话，你只需要通过下面的方式就能成功接收到服务端返回的结果。
        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }

        if(rpcRequestTransport instanceof SocketRpcClient){
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }

        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest){
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnums.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnums.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnums.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnums.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
