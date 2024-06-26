package org.example.myrpcframework.rpcFrameworkCommon.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoadBalanceEnums {
    CHLOADBALANCE("CHloadBalance"),
    RLOADBALANCE("RloadBalance");

    private final String name;
}
