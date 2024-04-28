package org.example.myrpcframework.rpcFrameworkSimple.remoting.dto;

import lombok.*;
import org.example.myrpcframework.rpcFrameworkCommon.enums.RpcResponseCodeEnums;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 715745410605631233L;
    private String requestId;
    /**
     * response code
     */
    private Integer code;
    /**
     * response message
     */
    private String message;
    /**
     * response body
     */
    private T data;


    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnums.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnums.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if(null != data){
            response.setData(data);
        }
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCodeEnums rpcResponseCodeEnums) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnums.getCode());
        response.setMessage(rpcResponseCodeEnums.getMessage());
        return response;
    }
}
