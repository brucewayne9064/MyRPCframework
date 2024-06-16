package org.example.myrpcframework.rpcFrameworkCommon.utils.threadpool;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);

        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaxPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getTimeUnit(), customThreadPoolConfig.getWorkQueue(),
                threadFactory);

    }

    //创建 ThreadFactory，用于产生新线程 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
    //daemon指定创建的线程是否是守护线程，守护线程是一种在没有非守护线程运行时自动结束的线程。
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon){
        if(threadNamePrefix != null){
            if(daemon != null){
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d") // threadNamePrefix-1，threadNamePrefix-2 这种线程名称
                        .setDaemon(daemon)
                        .build();
            } else {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .build();
            }
        }else return Executors.defaultThreadFactory();
    }


    //定期打印线程池的状态
    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool){
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(
                1,
                createThreadFactory("print-thread-pool-status", false));

        scheduledExecutorService.scheduleAtFixedRate(()->{
            log.info("============ThreadPool Status=============");
            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("Active Threads: [{}]", threadPool.getActiveCount());
            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            log.info("===========================================");
        }, 0 , 1, TimeUnit.SECONDS);
    }

}
