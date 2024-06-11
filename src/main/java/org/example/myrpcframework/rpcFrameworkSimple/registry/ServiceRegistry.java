package org.example.myrpcframework.rpcFrameworkSimple.registry;


import org.example.myrpcframework.rpcFrameworkCommon.extension.SPI;

import java.net.InetSocketAddress;

@SPI
public interface ServiceRegistry {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
