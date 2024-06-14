package org.example.myrpcframework.rpcFrameworkCommon.utils.threadpool;


import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
public class ThreadPoolFactoryUtil {

    /**
     * 通过 threadNamePrefix 来区分不同线程池（我们可以把相同 threadNamePrefix 的线程池看作是为同一业务场景服务）。
     * key: threadNamePrefix
     * value: threadPool
     */
    private static final Map<String, ExecutorService> THREAD_POOLS  = new ConcurrentHashMap<>();

    //私有化构造函数
    private ThreadPoolFactoryUtil() {

    }

    //daemon -> 守护进程/后台程序
    //
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix){

    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix){

    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){

    }


    //关闭所有线程池
    public static void shutDownAllThreadPool(){

    }

    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){

    }

    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon){

    }

    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool){

    }





}
