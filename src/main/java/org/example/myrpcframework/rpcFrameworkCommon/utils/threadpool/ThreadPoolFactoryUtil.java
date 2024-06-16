package org.example.myrpcframework.rpcFrameworkCommon.utils.threadpool;


import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;


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
    //根据配置文件和服务名称创建线程池，并且加入THREAD_POOLS
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix){
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix){
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){
        ExecutorService threadpool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k->
            createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon)
        );
        if(threadpool.isShutdown() || threadpool.isTerminated()){
            THREAD_POOLS.remove(threadNamePrefix);
            threadpool = createThreadPool(customThreadPoolConfig,threadNamePrefix,daemon);
            THREAD_POOLS.put(threadNamePrefix, threadpool);
        }
        return threadpool;
    }


    //关闭所有线程池
    public static void shutDownAllThreadPool(){
        log.info("call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry ->{
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            //isShutdown() 方法：此方法用来检查线程池是否进入关闭状态，不再接受新任务。
            // 如果线程池已经被调用了 shutdown() 或 shutdownNow() 方法，那么 isShutdown() 将返回 true，否则返回 false。

            //isTerminated() 方法：此方法用来检查所有提交的任务是否都已经完成。
            // 如果线程池已经关闭，并且所有任务都已完成执行，那么 isTerminated() 将返回 true。
            log.info("shut down thread pool [{}] [{}]", entry.getKey(), executorService.isShutdown());
            try{
                //尝试执行 awaitTermination 方法来等待线程池中的所有任务完成。
                // 如果在指定的时间内所有任务都完成了，那么 try 块中的代码将正常执行完毕。
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }

    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon){

    }

    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon){

    }

    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool){

    }





}
