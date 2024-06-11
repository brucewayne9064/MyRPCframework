package org.example.myrpcframework.rpcFrameworkSimple.config;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    private String version ="";

    private String group ="";

    private Object service;

    public String getRpcServiceName() {
        return  getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        //获取 service 对象实现的第一个接口的全名（Canonical Name）
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

}
