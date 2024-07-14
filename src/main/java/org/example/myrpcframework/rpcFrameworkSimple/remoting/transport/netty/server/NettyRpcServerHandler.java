package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.enums.CompressTypeEnums;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcResponseCodeEnums;
import org.example.myrpcframework.rpcFrameworkCommon.enums.SerializationTypeEnums;
import org.example.myrpcframework.rpcFrameworkCommon.factory.SingletonFactory;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.constants.RpcConstants;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcMessage;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcResponse;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.handler.RpcRequestHandler;


@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {
    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }


    /**
     * 读取从客户端消息，然后调用目标服务的目标方法并返回给客户端。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // 如果收到的是RpcMessage
            if (msg instanceof RpcMessage) {
                log.info("server receive msg: [{}] ", msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnums.HESSIAN.getCode());
                rpcMessage.setCompress(CompressTypeEnums.GZIP.getCode());


                // 如果收到的只是心跳检测，那就返一个回去
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                } else {
                    //收到的是 需要返回操作结果的request
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    // Execute the target method (the method the client needs to execute) and return the method result
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("server get result: %s", result.toString()));
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnums.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            //Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }

    // Netty 心跳机制相关。保证客户端和服务端的连接不被断掉，避免重连。
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
