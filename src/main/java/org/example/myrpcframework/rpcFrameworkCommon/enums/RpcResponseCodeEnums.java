package org.example.myrpcframework.rpcFrameworkCommon.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnums {
    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is failed");

    private final int code;

    private final String message;
}
