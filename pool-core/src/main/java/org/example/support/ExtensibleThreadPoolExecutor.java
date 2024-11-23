package org.example.support;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * 这个类是扩展的线程类，此时为原生的线程池提供记录任务耗时的操作
 */
public class ExtensibleThreadPoolExecutor extends ThreadPoolExecutor {
    /**
     * 构造函数，参数参考原生的线程池
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     */
    public ExtensibleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    //用来记录开始时间，进行线程之间的传递
    private final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        //此时这个方法会在原生的ThreadPoolExecutor 的 runnableTask.run();之前执行
        //记录当前的时间
        long startTime = currentTime();
        threadLocal.set(startTime);
    }



    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        //获取当前时间，然后减去开始时间
        //从线程本地map中得到任务的开始时间
        try {
            //从线程本地map中得到任务的开始时间
            Optional.ofNullable(threadLocal.get())
                    //计算出耗时时间
                    .map(startTime -> currentTime() - startTime)
                    //交给processTaskTime方法处理
                    .ifPresent(this::processTaskTime);
        } finally {
            //清除线程本地map
            threadLocal.remove();
        }
    }

    private void processTaskTime(Long time) {
        //TODO 可以进行告警的扩展，比如设置一个任务的执行阈值耗时，如果超过的话，此时就进行告警即可
    }

    protected long currentTime(){
        return System.currentTimeMillis();
    }
}
