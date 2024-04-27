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
│   │       │        ├── AliSmsService.java
│   │       │        ├── CglibProxyFactory.java
│   │       │        ├── Main.java
│   │       │        └── cglibProxy.java
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

## 一、什么是rpc

rpc是远程过程调用。

为什么需要RPC？ 两个不同服务器提供的服务不在同一个内存空间，需要网络才能传递方法调用的参数和方法调用的结果。 
rpc框架可以帮助我们调用远程服务器上的某个方法，且过程如同本地方法一样简单，不需要考虑底层的细节，例如底层传输方式，序列化方式等。

### 1.1 RPC核心部件

1. 客户端（服务消费端） ：调用远程方法的一端。

2. 客户端 Stub（桩，客户端存根，代理） ： 这其实就是一代理类。代理类主要做的事情很简单，就是把你调用方法、类、方法参数等信息传递到服务端。

3. 网络传输 ： 网络传输就是你要把你调用的方法的信息比如说参数传输到服务端，然后服务端执行完之后再把返回结果通过网络传输给你传输回来。网络传输的实现方式有很多种比如最基本的 Socket 或者性能以及封装更加优秀的 Netty（推荐）。

4. 服务端处理器：服务端的逻辑处理部分。在这一部分，服务端接收到客户端的请求后，会根据请求的内容调用相应的服务逻辑，执行实际的业务操作，并将结果返回给客户端。这个处理过程可能涉及到数据库访问、文件操作、业务计算等各种服务端需要执行的任务。

5. 服务端（服务提供端） ：提供远程方法的一端。

### 1.2 RPC调用流程

1. 服务消费端（client）以本地调用的方式调用远程服务；

2. 客户端 Stub（client stub） 接收到调用后负责将方法、参数等组装成能够进行网络传输的消息体（序列化）：RpcRequest；

3. 客户端 Stub（client stub） 找到远程服务的地址，并将消息发送到服务提供端；

4. 服务端 Stub（桩）收到消息将消息反序列化为 Java 对象: RpcRequest；

5. 服务端 Stub（桩）根据RpcRequest中的类、方法、方法参数等信息调用本地的方法；

6. 服务端 Stub（桩）得到方法执行结果并将组装成能够进行网络传输的消息体：RpcResponse（序列化）发送至消费方；

7. 客户端 Stub（client stub）接收到消息并将消息反序列化为 Java 对象:RpcResponse ，这样也就得到了最终结果。over!

## 什么是序列化

## Socket网络通信

## Netty网络通信

## 五、静态代理+JDK/CGLIB动态代理

### 5.1 代理模式

- 静态代理

1. 定义一个接口及其实现类

2. 创建一个代理类同样实现这个接口

3. 将目标对象注入进代理类，然后在代理类的对应方法调用目标类中的对应方法。 
   这样的话，我们就可以通过代理类屏蔽对目标对象的访问，并且可以在目标方法执行前后做一些自己想做的事情。
   通常是在原始类的基础上附加与原始类无关的功能，用于访问控制，或者监视，统计流量。

- 动态代理

无需声明式创建Java代理类，而是在运行过程中生成“虚拟”的代理类，被ClassLoader加载。从而避免静态代理那样需要声明大量的代理类。

  - JDK动态代理

    主要的两个核心类为：java.lang.reflect.Proxy和java.lang.reflect.InvocationHandler

      1. 定义一个接口及其实现类；
      2. 自定义 InvocationHandler 并重写invoke方法，在 invoke 方法中我们会调用原生方法（被代理类的方法）并自定义一些处理逻辑；
      3. 通过 Proxy.newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h) 方法创建代理对象；
    
    与静态代理相比，无论有多少接口，都只需要一个代理类。

  - CGLIB动态代理（Code Generation Library）

    然而JDK动态代理只能代理实现了接口的类，为了解决这个问题，可以使用CGLIB动态代理。


## ZooKeeper+Curator

- zookeeper

  ZooKeeper 是一个开源的分布式协调服务，它的设计目标是将那些复杂且容易出错的分布式一致性服务封装起来，构成一个高效可靠的原语集，并以一系列简单易用的接口提供给用户使用。
  ZooKeeper 为我们提供了高可用、高性能、稳定的分布式数据一致性解决方案，通常被用于实现诸如数据发布/订阅、负载均衡、命名服务、分布式协调/通知、集群管理、Master 选举、
  分布式锁和分布式队列等功能。这些功能的实现主要依赖于 ZooKeeper 提供的 数据存储+事件监听功能。

- Curator

  Curator 是Netflix公司开源的一套 ZooKeeper Java客户端框架，相比于 Zookeeper 自带的客户端 zookeeper 来说， 
  Curator 的封装更加完善，各种 API 都可以比较方便地使用。

## RPC网络传输模块

## RPC注册中心模块

## RPC其他模块

## RPC优化
