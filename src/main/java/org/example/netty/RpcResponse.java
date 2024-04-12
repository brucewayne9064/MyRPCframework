package org.example.netty;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcResponse { // 服务端响应实体类
    private String message;
}
