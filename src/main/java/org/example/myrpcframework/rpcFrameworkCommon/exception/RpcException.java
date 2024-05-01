package org.example.myrpcframework.rpcFrameworkCommon.exception;

import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcErrorMessageEnums;

public class RpcException extends RuntimeException{
    public RpcException(RpcErrorMessageEnums rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnums rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
