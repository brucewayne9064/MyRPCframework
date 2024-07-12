package org.example.myrpcframework.rpcFrameworkSimple.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

//传输协议中需要使用的一些头部信息和常量
public class RpcConstants {

    // Magic number. Verify RpcMessage，用于在RPC消息的开始处标识这是一个RPC消息。这有助于在网络传输中快速识别消息类型。
    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    // 定义了默认的字符集，用于字符编码转换，这里使用的是UTF-8，这是一种广泛使用的字符编码。
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    // version information, 定义了RPC协议的版本号，这有助于处理不同版本的兼容性问题。
    public static final byte VERSION = 1;
    // 定义了 ??? 的总长度，这里是16个字节。
    public static final byte TOTAL_LENGTH = 16;
    // 分别定义了请求和响应消息的类型标识。
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    // 定义了心跳检测的请求和响应类型，用于保持连接的活跃状态。
    // ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    // pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    // 定义了RPC消息头部的长度，这里也是16个字节，与TOTAL_LENGTH相同。
    public static final int HEAD_LENGTH = 16;
    // 分别代表心跳检测的请求和响应消息内容。
    public static final String PING = "ping";
    public static final String PONG = "pong";
    // 定义了RPC消息的最大长度，这里是8MB（8 * 1024 * 1024字节），用于限制单个消息的大小，避免过大的消息对系统性能造成影响。
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
}
