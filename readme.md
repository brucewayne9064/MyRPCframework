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
   
myrpcframework
├── MyRpCframeworkApplication.java
├── client
│     ├── CalculatorController.java
│     ├── NettyClientMain.java
│     └── SocketClientMain.java
├── rpcFrameworkCommon
│     ├── enums
│     ├── exception
│     ├── extension
│     ├── factory
│     └── utils
├── rpcFrameworkSimple
│     ├── annotation
│     ├── compress
│     ├── config
│     ├── loadbalance
│     ├── provider
│     ├── proxy
│     ├── registry
│     ├── remoting
│     ├── serialize
│     └── spring
├── server
│     ├── NettyServerMain.java
│     ├── SocketServerMain.java
│     └── serviceImpl
└── serviceAPIs
      ├── AddCalculatorService.java
      ├── Numbers.java
      └── SubCalculatorService.java

~~~

## 一、运行项目

### 导入项目

- git Clone项目到本地
- 在idea中打开

### 下载并运行zookeeper

- 安装Docker Desktop
- 下载zookeeper容器
    ```
    docker pull zookeeper:3.5.8
    ```
- 运行zookeeper
    ```
    docker run -d --name zookeeper -p 2181:2181 zookeeper:3.5.8
    ```

### 运行基于socket通信的版本

服务端入口位置
```
org/example/myrpcframework/server/SocketServerMain.java
```
需要在以下文件夹中自定义服务的接口
```
org/example/myrpcframework/serviceAPIs
```
在以下文件夹中实现服务实现接口
```
org/example/myrpcframework/server/serviceImpl
```

然后在SocketServerMain类的main函数中发布该服务，比如这里我注册了加法计算器
```java
public class SocketServerMain {
public static void main(String[] args) {
AddCalculatorService addCalculatorService = new AddCalculatorServiceImpl();
SocketRpcServer socketRpcServer = new SocketRpcServer();
RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();

        rpcServiceConfig.setService(addCalculatorService);

        socketRpcServer.registerService(rpcServiceConfig);

        socketRpcServer.start();
    }
}
```

客户端入口位置
```
org/example/myrpcframework/client/SocketClientMain.java
```
客户端是使用代理对象来处理网络连接的，可以直接使用服务端提供的加法服务进行计算
```java
public class SocketClientMain {
public static void main(String[] args) {
RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();

        //初始化代理类
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        //建立服务的代理对象
        AddCalculatorService addCalculatorService = rpcClientProxy.getProxy(AddCalculatorService.class);

        int result = addCalculatorService.add(new Numbers(12, 45)); // Numbers是用全参数构造器（由@AllArgsConstructor生成）进行构造的

        System.out.println(result);


    }
}
```

### 运行基于netty通信的版本
服务端主入口
```
org/example/myrpcframework/server/NettyServerMain.java
```

客户端主入口
```
org/example/myrpcframework/client/NettyClientMain.java
```