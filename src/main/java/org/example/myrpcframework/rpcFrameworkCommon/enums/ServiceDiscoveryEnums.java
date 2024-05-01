package org.example.myrpcframework.rpcFrameworkCommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceDiscoveryEnums {
    ZK("zk");

    private final String name;
}
