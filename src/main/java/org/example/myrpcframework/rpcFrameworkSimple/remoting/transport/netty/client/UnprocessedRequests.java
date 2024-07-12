package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.client;

import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


// 管理服务器尚未处理的RPC请求响应
// 这个类的主要作用是管理RPC调用的异步响应，通过 put 方法将请求ID和异步响应关联起来，
// 并在收到RPC响应时通过 complete 方法来完成对应的异步操作。如果尝试完成一个不存在的异步操作，则会抛出异常。
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
