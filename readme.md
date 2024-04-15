# My Remote Procedure Call framework

## 仓库目录结构

~~~
├── netty (netty通信)
│   └── kyro
│       ├── client (客户端)
│       │   ├── NettyClient.java
│       │   └── NettyClientHandler.java
│       ├── codec (编码器和解码器)
│       │   ├── NettyKryoDecoder.java
│       │   └── NettyKryoEncoder.java
│       ├── dto (传输实体类)
│       │   ├── RpcRequest.java
│       │   └── RpcResponse.java
│       ├── serialize (Kryo序列化实现)
│       │   ├── KryoSerializer.java
│       │   ├── SerializeException.java
│       │   └── Serializer.java
│       └── server (服务端)
│           ├── NettyServer.java
│           └── NettyServerHandler.java
└── socket (socket通信)
    ├── HelloClient.java (客户端)
    ├── HelloServer.java (服务端)
    └── Message.java (消息实体类)
~~~

