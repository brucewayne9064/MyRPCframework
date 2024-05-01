package org.example.myrpcframework.rpcFrameworkSimple.registry;

import org.example.myrpcframework.rpcFrameworkCommon.extension.SPI;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
