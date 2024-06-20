package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.factory.SingletonFactory;
import org.example.myrpcframework.rpcFrameworkCommon.utils.threadpool.ThreadPoolFactoryUtil;
import org.example.myrpcframework.rpcFrameworkSimple.config.CustomShutdownHook;
import org.example.myrpcframework.rpcFrameworkSimple.config.RpcServiceConfig;
import org.example.myrpcframework.rpcFrameworkSimple.provider.Impl.ZkServiceProviderImpl;
import org.example.myrpcframework.rpcFrameworkSimple.provider.ServiceProvider;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.server.NettyRpcServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer {
    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer(){
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
    }

    public void registerService(RpcServiceConfig rpcServiceConfig){
        serviceProvider.publishService(rpcServiceConfig);
    }


    public void start(){
        try(ServerSocket server = new ServerSocket()){
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, NettyRpcServer.PORT));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while((socket = server.accept())!=null){
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new );
            }
        } catch(IOException e){
            log.error("occur IOException:", e);
        }
    }
}
