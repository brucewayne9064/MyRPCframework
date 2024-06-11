package org.example.myrpcframework.rpcFrameworkSimple.loadbalance.loadbalancer;

import org.example.myrpcframework.rpcFrameworkSimple.loadbalance.AbstractLoadBalance;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

//它的作用是从提供的服务列表 serviceUrlList 中随机选择一个服务URL，并返回这个URL。
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceUrlList.get(random.nextInt(serviceUrlList.size()));
    }
}
