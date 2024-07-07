package org.example.myrpcframework.rpcFrameworkSimple.remoting.dto;


import lombok.*;

import java.io.Serializable;

@ToString
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String interfaceName;  // 接口名
    private String methodName;     // 方法名
    private Object[] parameters;   // 参数
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }

}
