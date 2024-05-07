package org.example.myrpcframework.rpcFrameworkSimple.loadbalance;


import org.example.myrpcframework.rpcFrameworkCommon.extension.SPI;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;

import java.util.List;

@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
