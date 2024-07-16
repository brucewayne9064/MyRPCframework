package org.example.myrpcframework.rpcFrameworkCommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcRequestTransportEnum {
    NETTY("netty"),
    SOCKET("socket");

    private final String name;
}
