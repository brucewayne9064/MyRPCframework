package org.example.netty.kyro.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.example.netty.kyro.codec.NettyKryoDecoder;
import org.example.netty.kyro.codec.NettyKryoEncoder;
import org.example.netty.kyro.serialize.KryoSerializer;
import org.example.netty.kyro.dto.RpcRequest;
import org.example.netty.kyro.dto.RpcResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class NettyClient {

    private final String host;
    private final int port;
    private static final Bootstrap b;
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public NettyClient(String host, int port){
        this.host = host;
        this.port = port;
    }


    // 初始化相关资源比如 EventLoopGroup, Bootstrap
    static {
        KryoSerializer kryoSerializer = new KryoSerializer();
        // 创建了一个Bootstrap实例，这是Netty客户端的启动类。
        b = new Bootstrap();
        //为客户端设置了一个EventLoopGroup，这是Netty中处理网络事件的核心组件。NioEventLoopGroup是基于NIO的事件循环组，负责处理网络I/O操作。在这里，它被用作客户端的网络操作处理组。
        b.group(new NioEventLoopGroup())
                //指定了客户端使用的通道类型为NioSctpChannel。这表明客户端将基于SCTP（Stream Control Transmission Protocol，流控制传输协议）进行通信。SCTP是一个面向消息的、可靠的、面向连接的传输层协议，通常用于需要可靠消息传输的场景。
                .channel(NioSctpChannel.class)
                //添加了一个日志处理器LoggingHandler，它用于记录网络操作的日志信息。LogLevel.INFO表示日志级别为INFO，这意味着只有INFO级别以上的日志信息会被记录和输出。
                .handler(new LoggingHandler(LogLevel.INFO))
                //设置了连接超时的时间为5000毫秒（5秒）。如果在5秒内客户端未能成功建立连接，那么连接尝试将被取消，并可能抛出超时异常。
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch){
                        /*
                         自定义序列化编解码器
                         */
                        // ByteBuf -> RpcResponse
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        // RpcRequest -> ByteBuf
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        //处理客户端消息，用于接收服务器发来的消息并把结果返回服务器
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }



    /*
    * 用于向服务端发送消息的 sendMessage()方法，通过这个方法你可以将消息也就是RpcRequest对象发送到服务端，
    * 并且你可以同步获取到服务端返回的结果也就是RpcResponse 对象。
    */
    public RpcResponse sendMessage(RpcRequest rpcRequest){
        try{
            ChannelFuture f;
        }catch(){

        }
        return null;
    }
}
