package org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.netty.codec;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.enums.CompressTypeEnums;
import org.example.myrpcframework.rpcFrameworkCommon.enums.SerializationTypeEnums;
import org.example.myrpcframework.rpcFrameworkCommon.extension.ExtensionLoader;
import org.example.myrpcframework.rpcFrameworkSimple.compress.Compress;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.constants.RpcConstants;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcMessage;
import org.example.myrpcframework.rpcFrameworkSimple.serialize.Serializer;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * <p>
 * custom protocol decoder
 * <p>
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 *
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */

@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    // 可以原子性操作的计数器，初始值为0
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);  // 魔法数
            out.writeByte(RpcConstants.VERSION);        // RPC协议的版本号
            // leave a place to write the value of full length
            out.writerIndex(out.writerIndex() + 4);  // 留位置写full length
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);                 // messageType
            out.writeByte(rpcMessage.getCodec());       // Codec序列化类型
            out.writeByte(CompressTypeEnums.GZIP.getCode());  // compress压缩类型: gzip
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());  //  RequestId
            // build full length
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // if messageType is not heartbeat message,fullLength = head length + body length
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // serialize the object
                String codecName = SerializationTypeEnums.getName(rpcMessage.getCodec());  // 得到序列化方式的名称
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);                                          //得到序列化器 kryo
                bodyBytes = serializer.serialize(rpcMessage.getData());  // 把正文部分序列化了
                // compress the bytes
                String compressName = CompressTypeEnums.getName(rpcMessage.getCompress()); //压缩方法名 gzip
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);                                       // 得到压缩器
                bodyBytes = compress.compress(bodyBytes);  // 把正文部分进行压缩
                fullLength += bodyBytes.length;  // 把全部长度加上正文长度
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);  // body
            }
            int writeIndex = out.writerIndex();  // 获取当前ByteBuf的写索引位置，即下一个要写入字节的位置。
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);  //回到full length那个字段的开头位置
            out.writeInt(fullLength); // 写入full length
            out.writerIndex(writeIndex); // 又回到最新位置
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }

    }
}
