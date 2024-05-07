package org.example.myrpcframework.rpcFrameworkCommon.utils;

import java.util.Collection;

//集合工具类，判断集合是否为空
public class CollectionUtil {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
