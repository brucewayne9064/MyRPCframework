package org.example.myrpcframework.rpcFrameworkCommon.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoadBalanceEnums {
    LOADBALANCE("loadBalance");

    private final String name;
}
