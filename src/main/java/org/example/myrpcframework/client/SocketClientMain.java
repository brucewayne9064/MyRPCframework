package org.example.myrpcframework.client;

import org.example.myrpcframework.rpcFrameworkSimple.config.RpcServiceConfig;
import org.example.myrpcframework.rpcFrameworkSimple.proxy.RpcClientProxy;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.RpcRequestTransport;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.socket.SocketRpcClient;
import org.example.myrpcframework.serviceAPIs.AddCalculatorService;
import org.example.myrpcframework.serviceAPIs.Numbers;

public class SocketClientMain {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();

        //初始化代理类
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        //建立服务的代理对象
        AddCalculatorService addCalculatorService = rpcClientProxy.getProxy(AddCalculatorService.class);

        int result = addCalculatorService.add(new Numbers(12, 45)); // Numbers是用全参数构造器（由@AllArgsConstructor生成）进行构造的

        System.out.println(result);


    }
}
