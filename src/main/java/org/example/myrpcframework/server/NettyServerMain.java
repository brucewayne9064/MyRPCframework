package org.example.myrpcframework.server;

import org.example.myrpcframework.rpcFrameworkSimple.annotation.RpcScan;
import org.example.myrpcframework.rpcFrameworkSimple.config.RpcServiceConfig;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.server.NettyRpcServer;
import org.example.myrpcframework.server.serviceImpl.SubCalculatorServiceImpl;
import org.example.myrpcframework.serviceAPIs.SubCalculatorService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"org.example.myrpcframework"})  //指定Spring框架扫描的包路径，以便自动注册一些特定的组件。
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        // 通过传递 NettyServerMain.class 来告诉Spring扫描 NettyServerMain 类中定义的注解。
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // Register service manually
        SubCalculatorService subCalculatorService = new SubCalculatorServiceImpl();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test2").version("version2").service(subCalculatorService).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
