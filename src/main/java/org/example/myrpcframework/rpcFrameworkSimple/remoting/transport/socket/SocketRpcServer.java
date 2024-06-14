package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.factory.SingletonFactory;
import org.example.myrpcframework.rpcFrameworkSimple.provider.Impl.ZkServiceProviderImpl;
import org.example.myrpcframework.rpcFrameworkSimple.provider.ServiceProvider;

import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer {
    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer(){
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.threadPool =
    }

    public void start(){

    }
}
