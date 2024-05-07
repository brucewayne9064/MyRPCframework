package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.socket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcErrorMessageEnums;
import org.example.myrpcframework.rpcFrameworkCommon.enums.ServiceDiscoveryEnums;
import org.example.myrpcframework.rpcFrameworkCommon.exception.RpcException;
import org.example.myrpcframework.rpcFrameworkCommon.extension.ExtensionLoader;
import org.example.myrpcframework.rpcFrameworkSimple.registry.ServiceDiscovery;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.RpcRequestTransport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


@Slf4j
@AllArgsConstructor
//客户端，用于发送rpc请求，基于socket传输信息
public class SocketRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;  // serviceDiscovery接口

    public SocketRpcClient(){
        // 使用ExtensionLoader来获取ServiceDiscovery类型的扩展点实例
        this.serviceDiscovery = ExtensionLoader
                // 获取ServiceDiscovery接口的扩展加载器
                .getExtensionLoader(ServiceDiscovery.class)
                // 获取名为ZK的扩展点实例
                .getExtension(ServiceDiscoveryEnums.ZK.getName());
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //得到服务端的socket地址（ip＋端口号）
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()) {  //创建一个新的Socket对象
            socket.connect(inetSocketAddress);  //连接到InetSocketAddress指定的服务地址

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // 通过output stream向服务端传输data
            objectOutputStream.writeObject(rpcRequest);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 通过input stream读取服务端的data
            return objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException(RpcErrorMessageEnums.SERVICE_INVOCATION_FAILURE);  //调用服务失败
        }
    }
}
