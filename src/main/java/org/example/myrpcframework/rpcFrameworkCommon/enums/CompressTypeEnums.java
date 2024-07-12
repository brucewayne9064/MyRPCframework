package org.example.myrpcframework.rpcFrameworkCommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressTypeEnums {
    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;


    // 通过code检索value
    public static String getName(byte code) {
        for (CompressTypeEnums c : CompressTypeEnums.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
