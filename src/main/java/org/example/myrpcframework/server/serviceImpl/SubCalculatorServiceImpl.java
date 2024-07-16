package org.example.myrpcframework.server.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.serviceAPIs.Numbers;
import org.example.myrpcframework.serviceAPIs.SubCalculatorService;

@Slf4j
public class SubCalculatorServiceImpl implements SubCalculatorService {
    static{
        System.out.println("SubCalculatorServiceImpl start");
    }

    @Override
    public int sub(Numbers numbers) {
        log.info("SubCalculatorServiceImpl 接收到两个数字， 分别是：{} 和 {}", numbers.getNumber1(), numbers.getNumber2());
        int result = numbers.getNumber1() - numbers.getNumber2();
        log.info("SubCalculatorServiceImpl 返回 {}", result);
        return result;
    }
}
