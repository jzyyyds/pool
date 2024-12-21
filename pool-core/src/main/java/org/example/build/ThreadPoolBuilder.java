package org.example.build;

import lombok.extern.slf4j.Slf4j;
import org.example.support.AbstractBuildThreadPoolTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
public class ThreadPoolBuilder implements Builder<ThreadPoolExecutor> {
    //默认的线程池的核心线程数量，这个数量是根据CPU核心数量计算出来的
    private int corePoolSize = calculateCore();

    //默认的池最大线程数量
    private int maxPoolSize = corePoolSize + (corePoolSize >> 1);

    //默认存活时间
    private long keepAliveTime = 30000L;

    //默认时间单位
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    //默认执行任务超时时间
    private long executeTimeOut = 10000L;

    //默认队列容量
    private int capacity = 512;

    //队列类型，默认使用ResizableCapacityLinkedBlockingQueue队列
    //TODO 后续改成枚举类
    private String blockingQueueType = "ResizableCapacityLinkedBlockingQueue";

     //线程池队列
    private BlockingQueue<Runnable> workQueue;

    //默认拒绝策略
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    //线程池现成的前缀名称
    private String threadNamePrefix;

    //创建线程的线程工厂
    private ThreadFactory threadFactory;

    //线程池Id
    private String threadPoolId;


    //线程池关闭时，默认等待剩余任务执行的最大时间
    private Long awaitTerminationMillis = 5000L;

    //线程池关闭时是否等待正在执行的任务执行完毕
    private Boolean waitForTasksToCompleteOnShutdown = true;

    //是否允许超过存活时间的核心线程终止工作
    private Boolean allowCoreThreadTimeOut = false;
    //是否为守护线程
    private boolean isDaemon = false;

    //得到线程池核心线程数量的方法
    private Integer calculateCore() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
    }

    //设置动态线程池线程名称前缀的方法
    public ThreadPoolBuilder threadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        return this;
    }


    //设置动态线程吃线程工厂的方法
    public ThreadPoolBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }


    //设置核心线程数量
    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }


    //设置最大线程数量
    public ThreadPoolBuilder maxPoolNum(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        if (maxPoolSize < this.corePoolSize) {
            this.corePoolSize = maxPoolSize;
        }
        return this;
    }



    //设置线程池执行任务超时时间
    public ThreadPoolBuilder executeTimeOut(long executeTimeOut) {
        this.executeTimeOut = executeTimeOut;
        return this;
    }


    //设置时间单位和线程最大空闲时间
    public ThreadPoolBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        return this;
    }


    //设置线程池任务队列类型和队列容量
    public ThreadPoolBuilder workQueue(String queueType, int capacity) {
        this.blockingQueueType = queueType;
        this.capacity = capacity;
        return this;
    }


    //设置拒绝策略
    public ThreadPoolBuilder rejected(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }


    //设置任务队列
    public ThreadPoolBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }


    //设置线程池Id
    public ThreadPoolBuilder threadPoolId(String threadPoolId) {
        this.threadPoolId = threadPoolId;
        return this;
    }



    //设置线程池关闭时，等待剩余任务执行的最大时间
    public ThreadPoolBuilder awaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        return this;
    }


    //设置线程池关闭时是否等待正在执行的任务执行完毕标志
    public ThreadPoolBuilder waitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
        return this;
    }



    //设置是否允许超过存活时间的核心线程终止工作的标志
    public ThreadPoolBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }


    //得到线程池构建器的方法
    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }

    private static ThreadPoolExecutor buildPool(ThreadPoolBuilder builder) {
        return AbstractBuildThreadPoolTemplate.buildDynamicPool(buildInitParam(builder));
    }

    @Override
    public ThreadPoolExecutor build() {
        return buildPool(this);
    }

    private static AbstractBuildThreadPoolTemplate.ThreadPoolInitParam buildInitParam(ThreadPoolBuilder builder) {
        //定义一个ThreadPoolInitParam对象
        AbstractBuildThreadPoolTemplate.ThreadPoolInitParam initParam;
        initParam = new AbstractBuildThreadPoolTemplate.ThreadPoolInitParam(builder.threadFactory);

        //接下来就要使用刚才得到的构造器对象给initParam中的其他成员变量赋值即可
        initParam.setCorePoolNum(builder.corePoolSize)
                //设置最大线程数
                .setMaxPoolNum(builder.maxPoolSize)
                //设置线程存活时间
                .setKeepAliveTime(builder.keepAliveTime)
                //设置队列容量
                .setCapacity(builder.capacity)
                //设置任务超时时间
                .setExecuteTimeOut(builder.executeTimeOut)
                //设置拒绝策略
                .setRejectedExecutionHandler(builder.rejectedExecutionHandler)
                //设置时间单位
                .setTimeUnit(builder.timeUnit)
                //设置核心线程超过存活时间是否存活
                .setAllowCoreThreadTimeOut(builder.allowCoreThreadTimeOut);
            //这里创建的就是动态线程池得到线程池Id
            String threadPoolId = Optional.ofNullable(builder.threadPoolId).orElse(builder.threadNamePrefix);
            //设置线程池Id
            initParam.setThreadPoolId(threadPoolId);
            //设置线程池关闭时是否等待正在执行的任务执行完毕
            initParam.setWaitForTasksToCompleteOnShutdown(builder.waitForTasksToCompleteOnShutdown);
            //设置线程池关闭时，等待剩余任务执行的最大时间
            initParam.setAwaitTerminationMillis(builder.awaitTerminationMillis);
            initParam.setWorkQueue(builder.workQueue);
        return initParam;
    }

}
