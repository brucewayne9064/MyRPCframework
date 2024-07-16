package org.example.myrpcframework.client;

import org.example.myrpcframework.rpcFrameworkSimple.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@RpcScan(basePackage = {"org.example.myrpcframework"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        CalculatorController calController = (CalculatorController) applicationContext.getBean("calculatorController");
        calController.test();
    }
}
