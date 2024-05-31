package org.example.myrpcframework.rpcFrameworkSimple.registry;

import org.example.myrpcframework.rpcFrameworkCommon.extension.SPI;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;


//服务发现
//根据提供的rpcRequest查找服务器
@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
