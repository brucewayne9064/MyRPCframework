package org.example.myrpcframework.rpcFrameworkCommon.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//如果对象已经创建，就直接返回；如果尚未创建，则通过反射创建一个新的实例，并将其存储在OBJECT_MAP中，然后返回。
// 这样做的好处是确保了每个类只有一个实例，并且可以在不同的线程中安全地访问这个实例。
public class SingletonFactory {
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    private SingletonFactory() {

    }

    public static <T> T getInstance(Class<T> clazz) {
        if(clazz == null){
            throw new IllegalArgumentException();
        }
        String key = clazz.toString();
        if(OBJECT_MAP.containsKey(key)){
            //clazz.cast()是一个类型转换操作，它将取出的值强制转换为clazz指定的类型
            return clazz.cast(OBJECT_MAP.get(key));
        }else{
            return clazz.cast(OBJECT_MAP.computeIfAbsent(key, k ->{
                try{
                    return clazz.getDeclaredConstructor().newInstance(); //通过反射做到的
                } catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
                    throw new RuntimeException(e.getMessage() , e);
                }
            }));
        }
    }
}
