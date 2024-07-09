package org.example.netty.Demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Discards any incoming data.
 */
public class DiscardServer {
    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception{
        // NioEventLoopGroup is a multithreaded event loop that handles I/O operation
        // 'boss', accepts an incoming connection
        // 'worker', handles the traffic of the accepted connection once the boss accepts the connection
        // and registers the accepted connection to the worker.
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            //ServerBootstrap is a helper class that sets up a server.
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3) instantiate a new Channel to accept incoming connections.
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4) 指定了一个新的 Channel（通道）被接受时，应该使用哪个 ChannelInitializer 来初始化这个通道
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //使用 addLast 方法将一个新的 ChannelHandler 添加到管道的末尾。
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)   // (5) 设置 Channel 的配置选项。 SO_BACKLOG=128 服务器可以挂起的未处理连接的最大数量。
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  // (6) 设置child Channel 的配置选项

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 8080;
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }
        new DiscardServer(port).run();
    }
}
