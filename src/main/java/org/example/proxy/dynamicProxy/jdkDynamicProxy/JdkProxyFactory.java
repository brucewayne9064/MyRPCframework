package org.example.proxy.dynamicProxy.jdkDynamicProxy;

import java.lang.reflect.Proxy;


public class JdkProxyFactory {
    public static Object getProxy(Object target) {
        //Proxy中的newProxyInstance用于生成代理对象
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(), // 目标类的类加载
                target.getClass().getInterfaces(),  // 代理需要实现的接口，可指定多个
                new JdkProxy(target)   // 代理对象对应的自定义 InvocationHandler
        );
    }
}