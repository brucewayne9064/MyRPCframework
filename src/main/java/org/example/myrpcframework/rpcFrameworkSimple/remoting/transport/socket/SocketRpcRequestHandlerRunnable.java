package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.handler.RpcRequestHandler;

import java.net.Socket;

@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable{
    private final Socket socket;
    private final RpcRequestHandler;

    @Override
    public void run() {

    }
}
