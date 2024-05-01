package org.example.myrpcframework.rpcFrameworkCommon.extension;

import java.lang.annotation.*;

@Documented  //SPI注解将被包含在JavaDoc中，这意味着当生成Java文档时，SPI注解的信息也会被包含进去
@Retention(RetentionPolicy.RUNTIME)  //注解的保留策略，RetentionPolicy.RUNTIME表示在运行时可用
@Target(ElementType.TYPE)  // 指定了注解可以标记的目标类型，ElementType.TYPE表示它可以标记在类、接口（包括注解类型）的声明上
public @interface SPI {
}

//该注解被用于标记服务提供者接口，并且可以被保留到运行时，允许开发者通过反射等机制在运行时发现和加载服务提供者。
