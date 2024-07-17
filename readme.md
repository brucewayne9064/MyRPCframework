# My Remote Procedure Call framework

## 零、仓库目录结构

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
    
    

├── proxy (代理)
│   ├── dynamicProxy (动态代理)
│   │       ├── cglibDynamicProxy (CGLIB)
│   │       │        ├── AliSmsService.java (短信类)
│   │       │        ├── CglibProxyFactory.java (代理工厂类)
│   │       │        ├── Main.java
│   │       │        └── cglibProxy.java (动态代理类)
│   │       └── jdkDynamicProxy (JDK)
│   │           ├── JdkProxy.java (JDK 动态代理类)
│   │           ├── JdkProxyFactory.java (获取代理对象的工厂类)
│   │           ├── MailService.java (发送邮件接口)
│   │           ├── MailServiceImpl.java (发送邮件实现类)
│   │           ├── Main.java
│   │           ├── SmsService.java (发送短信接口)
│   │           └── SmsServiceImpl.java (发送短信实现类)
│   └── staticProxy (静态代理)
│           ├── MailProxy.java (邮件代理)
│           ├── MailService.java (发送邮件接口)
│           ├── MailServiceImpl.java (发送邮件实现类)
│           ├── Main.java
│           ├── SmsProxy.java (短信代理)
│           ├── SmsService.java (发送短信接口)
│           └── SmsServiceImpl.java (发送短信实现类)
└── zookeeper (zookeeper常用命令)
        └── Main.java
~~~
