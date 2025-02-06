# 动态线程池
### 项目介绍
#### 简介
本项目是一个动态线程池项目，可以通过spring boot starter的形式引入到业务项目里面,自带管理端，便于监控和显示动态线程池的信息，动态的修改线程池的参数信息，并且支持业务项目通过自定义插件的形式来对动态线程池进行增强，并且提供3种不同的告警模式，可以支持动态配置，解决并发业务中实际环境复杂，线程池参数不易设置恰当，而重新设置参数却需要重启系统的弊端

#### 技术选型
Spring Boot
Redis

#### 项目预览
![img.png](photot/img.png)
![img.png](photot/img_1.png)

#### 配置文件
```
spring:
  #如果想要邮件通知才进行配置，不然不需要
  mail:
    # 下面这个是QQ邮箱host ， 企业邮箱 smtp.exmail.qq.com
    host: smtp.qq.com
  # tencent mail port  这个是固定的
  port: 465
  # 你的QQ邮箱
  username: ********@qq.com
  # 进入邮箱配置后得到的授权码
  password: ************
  test-connection: true
  properties:
    mail:
      smtp:
        ssl:
          enable: true
# 动态线程池管理配置
dynamic:
  thread:
    pool:
      config:
        # 状态；true = 开启、false 关闭
        enabled: true
        # redis host
        host: 127.0.0.1
        # redis port
        port: 6379
        #可选
        password: 123456
dynamic-thread-pool:
  alarm:
   #是否开启告警功能
    enable: true
   #想要推送的平台，支持email,feishu,dingding
    use-platform: dingding
    webhook:
       #飞书和钉钉机器人的webhook
      feishu: **********
      dingding: ***********
```

#### 用户自定义扩展插件
当前组件内置了2个比较简单的插件：

1.当用户执行拒绝策略的时候，此时会触发告警通知

2.用户给线程池的任务设置超时时间，当任务执行超时，此时会触发告警通知
如果用户想要使用这些插件的话
```
ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.builder().threadPoolId("jzy")
                .corePoolSize(25)
                .maxPoolNum(50)
                .awaitTerminationMillis(10L)
                .threadFactory("jzy")
                .allowCoreThreadTimeOut(false)
                .waitForTasksToCompleteOnShutdown(true)
                .keepAliveTime(10, TimeUnit.SECONDS)
                .workQueue(new ResizableCapacityLinkedBlockingQueue<>(200))
                .rejected(new ThreadPoolExecutor.CallerRunsPolicy())
                .threadFactory(Executors.defaultThreadFactory())
                #设置任务超时的时间
                .executeTimeOut(100000000)
                #配置是否开启插件的开关
                .enable(true)
                .build();
```
此时还支持用户自定义，如果有其他需要扩展的需求，execute,reject,shutdown,shutdownNow等方法可以进行扩展
```
execute方法： 实现ExecuteAwarePlugin接口
reject方法：实现RejectedAwarePlugin接口
shutdown和shutdownNow方法：实现ShutdownAwarePlugin接口
```
实现完成之后，就可以将其注入进来
```
DefultThreadPoolPluginManager defultThreadPoolPluginManager = new DefultThreadPoolPluginManager();
        defultThreadPoolPluginManager.register(plugin);
```

#### 监控
基于Prometheus 和 Grafana 来进行搭建
![img.png](photot/img_2.png)