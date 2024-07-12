package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.enums.ServiceDiscoveryEnums;
import org.example.myrpcframework.rpcFrameworkCommon.extension.ExtensionLoader;
import org.example.myrpcframework.rpcFrameworkCommon.factory.SingletonFactory;
import org.example.myrpcframework.rpcFrameworkSimple.registry.ServiceDiscovery;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.RpcRequestTransport;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.codec.RpcMessageEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    //Client构造函数
    public NettyRpcClient() {
        // initialize resources such as EventLoopGroup, Bootstrap
        this.bootstrap = new Bootstrap();  //启动工具
        this.eventLoopGroup = new NioEventLoopGroup();  //事件循环器
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //  The timeout period of the connection.
                //  If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {  //新连接的handler
                    @Override
                    protected void initChannel(SocketChannel socketChannel){
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent ？
                        pipeline.addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageEncoder());  //编码器
                        pipeline.addLast(new RpcMessageDecoder());  //解码器
                        pipeline.addLast(new NettyRpcClientHandler());  //连接处理器
                    }
                });

        //服务发现器
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension(ServiceDiscoveryEnums.ZK.getName());
        //未处理request
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        //channel存储
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        return null;
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress){
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress){
        return null;
    }

    public void close(){

    }
}
