package org.example.myrpcframework.rpcFrameworkSimple.annotation;

import java.lang.annotation.*;

//RPC service annotation, marked on the service implementation class

// 注册服务
@Documented  // 指定某个注解应该被包含在JavaDoc中
@Retention(RetentionPolicy.RUNTIME)  // 指定注解的保留策略。RetentionPolicy.RUNTIME表示这个注解在运行时仍然有效
@Target({ElementType.TYPE})  //用于指定注解可以应用于哪些代码元素。ElementType.TYPE表示这个注解只能应用于类、接口或枚举
@Inherited  // 指定一个类注解是可继承的
public @interface RpcService {
    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";
}
