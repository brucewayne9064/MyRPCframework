package org.example.myrpcframework.rpcFrameworkSimple.annotation;

import org.example.myrpcframework.rpcFrameworkSimple.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})  // RpcScan 可以应用于类型（类、接口、枚举）和方法
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时通过反射仍然可以访问
@Import(CustomScannerRegistrar.class)  // 使用 RpcScan 注解时，会自动导入 CustomScannerRegistrar 类
@Documented  // RpcScan 应该被JavaDoc工具记录在生成的API文档中
public @interface RpcScan {
    String[] basePackage();
}
