package org.example.myrpcframework.rpcFrameworkCommon.extension;


import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkCommon.utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;


//Java服务提供者发现机制的实现，它模仿了Java标准库中的ServiceLoader类，但是提供了一些自定义的功能
//基本流程是先调用getExtensionLoader方法，输入我们需要的某种接口的实现加载器的class，返回这种接口的实现加载器
//然后调用getExtension，输入我们要的实现名称，返回该实现的实例
@Slf4j
public final class ExtensionLoader<T> {
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";  //定义了服务提供者配置文件的目录路径

    //EXTENSION_LOADER缓存，存放的是ExtensionLoader的实例对象
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    //EXTENSION_INSTANCES缓存，存放的是我们需要的实现的实例对象
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    //指定要用哪一个接口的loader
    private final Class<?> type;

    //cachedInstances缓存，是一个map，存放的是我们需要的实现名称，实现实例（存在一个holder里面）pair
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    //cachedClasses缓存，存放了一个holder，holder里面存了一个hashmap，hashmap上是从指定文件遍历出来的实现类类名和类的pair
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    //得到加载器ExtensionLoader实例
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }

        //先查找缓存里是否有这种type的extension loader，如果有直接返回。如果没有，创建一个，放到缓存里<type, extension loader>, 然后再从缓存里根据type查出来
        //缓存用的数据结构是concurrenthashmap
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    //得到实现实例
    public T getExtension(String name) {
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("Extension name 不能为 null 或者 空.");
        }

        //从缓存里查找holder实例，如果查不到，先创建放到缓存，在查找
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        // create a singleton if no instance exists
        //如果该holder的value为null
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);  //那么在这得到一个value
                    holder.set(instance); //set到holder上
                }
            }
        }
        return (T) instance;  //返回这个实例
    }

    private T createExtension(String name) {
        // load all extension classes of type T from file and get specific one by name
        Class<?> clazz = getExtensionClasses().get(name);  // 在这个hashmap里面找name的value
        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());  //创建新的实现的实例，类似new，不过这是通过反射来创建类的实例，它允许在运行时动态地创建对象
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        // get the loaded extension class from the cache
        Map<String, Class<?>> classes = cachedClasses.get();
        // double check
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();  //创建了一个hashmap用来放class
                    // load all extensions from our extensions directory
                    loadDirectory(classes);
                    cachedClasses.set(classes);  //把这个hashmap放到缓存里
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        //META-INF/extensions/org.example.myrpcframework.rpcFrameworkSimple.registry.ServiceDiscovery
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();  //存储的路径加上Class对象所表示的实体（类、接口、数组类、基本类型等）的全限定名。
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);  //获取资源URL
            //遍历资源url
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);  //把resourceUrl的名称和类名存到map
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    //第一个参数是map，第二个是classLoader，第三个参数是url
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            // read every line
            while ((line = reader.readLine()) != null) {
                // get index of comment
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // string after # is comment so we ignore it
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('='); //找到等于号的位置
                        String name = line.substring(0, ei).trim();  //等于号之前是name
                        String clazzName = line.substring(ei + 1).trim();  //等于号之后的是类名clazzName
                        // our SPI use key-value pair so both of them must not be empty
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name, clazz);  //map存放类名和对应的类
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}


