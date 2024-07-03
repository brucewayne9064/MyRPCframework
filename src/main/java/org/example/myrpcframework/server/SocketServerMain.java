package org.example.myrpcframework.server;

import org.example.myrpcframework.rpcFrameworkSimple.config.RpcServiceConfig;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.socket.SocketRpcServer;
import org.example.myrpcframework.server.serviceImpl.AddCalculatorServiceImpl;
import org.example.myrpcframework.serviceAPIs.AddCalculatorService;

public class SocketServerMain {
    public static void main(String[] args) {
        AddCalculatorService addCalculatorService = new AddCalculatorServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();

        rpcServiceConfig.setService(addCalculatorService);

        socketRpcServer.registerService(rpcServiceConfig);

        socketRpcServer.start();
    }
}
