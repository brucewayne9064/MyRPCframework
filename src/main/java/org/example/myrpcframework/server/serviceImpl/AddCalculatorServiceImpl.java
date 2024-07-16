package org.example.myrpcframework.server.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkSimple.annotation.RpcService;
import org.example.myrpcframework.serviceAPIs.AddCalculatorService;
import org.example.myrpcframework.serviceAPIs.Numbers;

@Slf4j
@RpcService(group = "test1", version = "version1")  //用这个注解的话，这个服务会被自动注册
public class AddCalculatorServiceImpl implements AddCalculatorService {

    static{
        System.out.println("AddCalculatorServiceImpl start");
    }

    @Override
    public int add(Numbers numbers) {
        log.info("AddCalculatorServiceImpl 接收到两个数字， 分别是：{} 和 {}", numbers.getNumber1(), numbers.getNumber2());
        int result = numbers.getNumber1() + numbers.getNumber2();
        log.info("AddCalculatorServiceImpl 返回 {}", result);
        return result;
    }
}
