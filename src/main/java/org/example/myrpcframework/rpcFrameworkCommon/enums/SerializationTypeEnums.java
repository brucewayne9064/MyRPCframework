package org.example.myrpcframework.rpcFrameworkCommon.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializationTypeEnums {
    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN((byte) 0X03, "hessian");


    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnums type : SerializationTypeEnums.values()) {
            if (type.getCode() == code) {
                return type.getName();
            }
        }
        return null;
    }


}
