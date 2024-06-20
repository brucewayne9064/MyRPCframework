package org.example.myrpcframework.rpcFrameworkSimple.config;


import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.utils.threadpool.ThreadPoolFactoryUtil;
import org.example.myrpcframework.rpcFrameworkSimple.registry.zk.util.CuratorUtil;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.server.NettyRpcServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


//关闭钩子（Shutdown Hooks）：
// 在Java中，关闭钩子是一种特殊的线程，它在JVM关闭之前执行。
//开发者可以在应用程序运行期间注册这些钩子，以便在JVM正常或非正常关闭时执行一些清理操作，如关闭数据库连接、释放资源等。

@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    //单例模式（饿汉模式）返回唯一对象
    public static CustomShutdownHook getCustomShutdownHook(){
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll(){
        log.info("addShutdownHook for clearAll");

        // 调用 Runtime.getRuntime().addShutdownHook() 方法，添加了一个在JVM关闭时执行的线程。
        // 这个线程是一个匿名内部类 Thread，它的 run 方法中定义了要执行的代码。
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try{
                //获取本地主机的地址和端口号
                InetSocketAddress inetSocketAddress =
                        new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                //清除在ZooKeeper注册中心注册的特定服务器上的所有服务
                CuratorUtil.clearRegistry(CuratorUtil.getZkClient(), inetSocketAddress);
            }catch (UnknownHostException ignored){

            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}
