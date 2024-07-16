package org.example.myrpcframework.rpcFrameworkSimple.annotation;


import java.lang.annotation.*;

//RPC reference annotation, autowire the service implementation class

// 消费服务
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})   // 字段
@Inherited
public @interface RpcReference {
    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";
}
