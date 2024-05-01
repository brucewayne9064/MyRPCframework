package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport;

import org.example.myrpcframework.rpcFrameworkCommon.extension.SPI;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;


@SPI
//发送 RPC 请求的顶层接口，分别使用 Socket 和 Netty 两种方式对这个接口进行实现
public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}
