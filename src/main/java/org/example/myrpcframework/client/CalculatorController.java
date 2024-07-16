package org.example.myrpcframework.client;

import org.example.myrpcframework.rpcFrameworkSimple.annotation.RpcReference;
import org.example.myrpcframework.serviceAPIs.Numbers;
import org.example.myrpcframework.serviceAPIs.SubCalculatorService;
import org.springframework.stereotype.Component;

@Component
public class CalculatorController {
    // 这个注解是为了自动配置需要的服务版本
    @RpcReference(version = "version2", group = "test2")
    private SubCalculatorService subCalculatorService;

    public void test() throws InterruptedException {
        int result = this.subCalculatorService.sub(new Numbers(10,1));
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        //assert "Hello description is 222".equals(hello);
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(subCalculatorService.sub(new Numbers(10,i)));
        }
    }
}
