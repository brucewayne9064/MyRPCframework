package org.example.netty.Demo;
import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter{

    //This method is called with the received message, whenever new data is received from a client.
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
//        // Discard the received data silently.
//        ((ByteBuf) msg).release(); // (3)

        ByteBuf in = (ByteBuf) msg;
        try{
            while (in.isReadable()){
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    // The method is called with a Throwable when an exception was raised by Netty due to an I/O error or by a handler
    // implementation due to the exception thrown while processing events.
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
