package org.example.proxy.dynamicProxy.cglibDynamicProxy;

public class Main {

    public static void main(String[] args) {
        AliSmsService aliSmsService = (AliSmsService) CglibProxyFactory.getProxy(AliSmsService.class);
        aliSmsService.send("java");
    }

}
