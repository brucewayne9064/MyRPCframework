package org.example.proxy.dynamicProxy.cglibDynamicProxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class cglibProxy implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        //调用方法前，可以添加自己的操作
        System.out.println("before method");
        Object result = methodProxy.invokeSuper(o, objects);
        //调用方法后，可以添加自己的操作
        System.out.println("after method");
        return result;
    }
}
