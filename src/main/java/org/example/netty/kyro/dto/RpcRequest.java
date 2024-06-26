package org.example.netty.kyro.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcRequest {  //客户端请求实体类
    private String interfaceName;
    private String methodName;
}
