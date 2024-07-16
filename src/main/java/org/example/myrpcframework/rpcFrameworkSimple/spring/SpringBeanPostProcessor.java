package org.example.myrpcframework.rpcFrameworkSimple.spring;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcRequestTransportEnum;
import org.example.myrpcframework.rpcFrameworkCommon.extension.ExtensionLoader;
import org.example.myrpcframework.rpcFrameworkCommon.factory.SingletonFactory;
import org.example.myrpcframework.rpcFrameworkSimple.annotation.RpcReference;
import org.example.myrpcframework.rpcFrameworkSimple.annotation.RpcService;
import org.example.myrpcframework.rpcFrameworkSimple.config.RpcServiceConfig;
import org.example.myrpcframework.rpcFrameworkSimple.provider.Impl.ZkServiceProviderImpl;
import org.example.myrpcframework.rpcFrameworkSimple.provider.ServiceProvider;
import org.example.myrpcframework.rpcFrameworkSimple.proxy.RpcClientProxy;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.transport.RpcRequestTransport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
// 如果你不将 SpringBeanPostProcessor 类标记为 @Component，你需要确保通过其他方式（如配置类中的 @Bean 方法）显式地注册这个 BeanPostProcessor，
// 以便让它参与到Spring的bean生命周期管理中，并确保其方法在适当的时机被调用。如果不这样做，SpringBeanPostProcessor 中定义的逻辑将不会被自动执行。
// call this method before creating the bean to see if the class is annotated
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension(RpcRequestTransportEnum.NETTY.getName());
    }


    // Spring bean 在实例化之前会调用 postProcessBeforeInitialization()方法
    // 我们可以在postProcessBeforeInitialization()方法中去判断类上是否有RpcService 注解。
    // 如果有的话，就取出 group 和 version 的值。
    // 然后，再调用 ServiceProvider 的 publishService() 方法发布服务即可！
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // get RpcService annotation
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }


    // 在 Spring bean 实例化之后会调用  postProcessAfterInitialization() 方法。
    // 我们可以在 postProcessAfterInitialization() 方法中遍历类的属性上是否有  RpcReference 注解。
    // 如果有的话，我们就通过反射将这个属性赋值即可！
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
