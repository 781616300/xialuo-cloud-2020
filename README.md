# xialuo-cloud-2020 订单➡支付➡库存➡积分➡物流➡仓储

# 5.eureka
#### 1.简介
```yaml
什么是服务注册与发现
Eureka采用了CS的设计架构，Eureka Server作为服务注册功能的服务器，它是服务注册中心。而系统中的其他微服务，使用Eureka的客户端连接到Eureka Server并维持心
跳连接。这样系统的维护人员就可以通过Eureka Server 来监控系统中各个微服务是否正常运行。
在服务注册与发现中，有一个注册中心。当服务器启动的时候，会把当前白己服务器的信息比却服务地址通讯地址等以别名方式注册到注册中心上。另-方(消费者服务提供
者)，以该别名的方式去注册中心上获取到实际的服务通讯地址，然后再实现本地RPC调用RPC远程调用框架核心设计思想:在于注册中心，因为使用注册中心管理每个服务与
服务之间的一个依赖关系(服务治理概念。在任何rpc远程框架中，都会有一个注册中心(存放服务地址相关信息(按口地址》
```
#### 2.eureka两大组件
```yaml
Eureka包含两个组件: Eureka Server和Eureka Client
Eureka Server提供服务注册服务
各个微服务节点通过配置启动后，会在EurekaServer中进行注册， 这样EurekaServer中的服务注册表中将会存储所有可用服务节点的
信息，服务节点的信息可以在界面中直观看到。

EurekaClient通过注册中心进行访问
是一个Java客户端，用于简化Eureka Server的交互,客户端同时也具备一个内置的、 使用轮询(round-robin)负载算法的负载均衡器
。在应用启动后，将会向Eureka Server发送心跳(默认周期为30秒)。如果Eureka Server在多个心跳周期内没有接收到某个节点的心
跳，EurekaServer将 会从服务注册表中把这个服务节点移除(默认90秒)
```
#### 3.erueka集群说明
```yaml
1先启动eurek注册中心
2启动服野提供者payment支付服务
3支付服务启动后会把自身信息(比如服务地以国名方式注册遊eureka)
4消费者order服务在需要调用接口时，使用服务别名去注册中心研取实际的RPC远程调用地址
5消费者获得调用地址后，底层实际是利用HtpClien技术实现远程调用
6消费者获得服努地址后会缓存在本地vm内存中，默认梅间隔30秒更新-次服务调用地址
```
#### 4。负载均衡 
```yaml
使用@LoadBalanced注解赋予RestTemplate负载均衡的能力
自带ribbon
```
#### 5.actuator微服务信息完善/访问信息有IP信息提示
```yaml
主机名称:服务名称修改
eureka:
  client:
    #表示是否将自己注册进EurekaServer默认为true。
    register-with-eureka: true
    #是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      #单机版
#      defaultZone: http://localhost:7001/eureka
#      # 集群版
  instance:
    instance-id: payment8002
    #访问路径可以显示IP地址
    prefer-ip-address: true
```
#### 6.服务发现Discovery
```java
//TODO 对于注册eureka里面的微服务,可以通过服务发现来获得该服务的信息
//TODO 启动类@EnableDiscoveryClient
@Slf4j
public class PaymentController
{
    @Resource
    private DiscoveryClient discoveryClient;

    @GetMapping(value = "/payment/discovery")
    public Object discovery()
    {
        List<String> services = discoveryClient.getServices();
        for (String element : services) {
            log.info("*****element: "+element);
            List<ServiceInstance> instances = discoveryClient.getInstances(element);
            for (ServiceInstance instance : instances) {
                log.info(instance.getServiceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
            }
        }
        return this.discoveryClient;
    }
}
```
#### 7.eureka自我保护：属于CAP里面的AP分支
```yaml
自我保护机制:默认情况下EurekaClent定时向EurekaServer端发送心跳包
如果Eureka在server端在一定时间内(默认90秒没有收到EurekaClent发送心跳包， 便会
直接从服务注册列表中剔除该服务。但是在规时间(90秒中)内丢失了大量的服务实例心跳。
这时候EurekaServer会开启自我保护机制，不会国该服务(读现象可能出现在如果网络不通
但是EurekaClent为出现老机此时如果换做别的注册中心如果一 定时间内没有收到心既会将
副附该服务,这样就出现了严重失误，因为喜户端还能正常发送心跳.只是网络延迟问题。而
保护机制是为了解决此问题而产生的)
```

# 6.zookeeper:SpringCloud整合Zookeeper替代Eureka
#### 1.pom
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
    <!--先排除自带的zookeeper3.5.3-->
    <exclusions>
        <exclusion>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!--添加zookeeper3.4.9版本-->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.9</version>
</dependency>
```

#### 2.
```yaml
#服务别名----注册zookeeper到注册中心名称
spring:
  application:
    name: cloud-provider-payment
  cloud:
    zookeeper:
      connect-string: 59.110.213.162:2181
```

### 3.服务节点是临时节点还是持久节点 :临时节点

# 7.consul
#### 1.简介
```yaml
1 是什么：Consul是一 套开源的分布式服务发现和配置管理系统，由HashiCorp公司用Go语言开发。
2 提供了微服务系统中的服务治理、配中心、控制总线等功能。这些功能中的每一个都可以根据需要单独使用，也可以-起使用以构建全方位的服
务网格，总之Consul提供了-种完整的服务网格解决方案。
```
#### 2.下载地址：https://www.consul.io/downloads.html
#### 3.使用开发模式启动:consul agent -dev 通过以下地址可以访问Consul的首页: http://localhost:8500
#### 4.pom
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```
```yaml
spring:
  application:
    name: consul-provider-payment
####consul注册中心地址
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        hostname: 127.0.0.1
        service-name: ${spring.application.name}
```

# 三个注册中心异同点
```yaml
eureka java语言 AP 支持健康检查 对外暴露http接口
consul go语言   CP 支持健康检查 对外暴露http接口
zk     java语言 CP 支持健康检查 客户端形式
```

# 8.ribbon 
#### 1是什么: Ribbon其实就是一个软负载均衡的客户端组件,  他可以和其他所需请求的客户端结合使用,和eureka结合只是其中一个实例.
#### 2底层 @LoadBalanced+RestTemplate
#### 3postForObject/postEntity
返回对象为ResponseEntity对象，包含了响应中的一些重要信息，比如响应头、响应状态码、响应体等
#### 4Ribbon核心组件IRule:IRule:根据特定算法从服务列表中选取一个要访问的服务
```yaml
com.netflix.loadbalancer.RoundRobinRule  轮询
com.netflix.loadbalancer.RandomRule  随机
com.netflix.loadbalancer.RetryRule
  先按照RoundRobinRule的策略获取服务,如果获取服务失败则在指定时间内进行重试,获取可用的服务
WeightedResponseTimeRule
  对RoundRobinRule的扩展,响应速度越快的实例选择权重越多大,越容易被选择
BestAvailableRule
  会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务,然后选择一个并发量最小的服务
AvailabilityFilteringRule
  先过滤掉故障实例,再选择并发较小的实例
ZoneAvoidanceRule
  默认规则,复合判断server所在区域的性能和server的可用性选择服务器
```
#### 5.如何替换
```java
官方文档明确给出了警告:
这个自定义配置类不能放在@ComponentScan所扫描的当前包下以及子包下，
否则我们自定义的这个配置类就会被所有的Ribbon客户端所共享,达不到特殊化定制的目的了。
主启动：@RibbonClient(name = "CLOUD-PAYMENT-SERVICE", configuration = MySelfRule.class) 

@Configuration
public class MySelfRule
{
    @Bean
    public IRule myRule()
    {
        return new RandomRule();//定义为随机
    }
}
```
#### 6.负载均衡算法: rest接口第几次请求数%服务器集群总数量=实际调用服务器位置下标，每次服务 重启动后rest接口计数从1开始。
#### 7.ribbon文章
- [负载均衡Ribbon底层实现](https://blog.csdn.net/hezhezhiyule/article/details/84846578 "负载均衡Ribbon底层实现")

# 9.openfeign
#### 1.是什么：Feign是一个声明式的Web服务客户端,让编写Web服务客户端变得非常容易,只需  创建一个接口并在接口上添加注解即可
#### 2。主启动@EnableFeignClients
#### 3。业务类@FeignClient
#### 4。Feign自带负载均衡配置项 底层就是ribbon
#### 5。OpenFeign超时控制 OpenFeign默认等待1秒钟,超过后报错
```yaml
ribbon:
#指的是建立连接所用的时间，适用于网络状况正常的情况下,两端连接所用的时间
  ReadTimeout: 5000
#指的是建立连接后从服务器读取到可用资源所用的时间
  ConnectTimeout: 5000
```
#### 6。OpenFeign日志打印功能
```java
@Configuration
public class FeignConfig
{
    @Bean
    Logger.Level feignLoggerLevel()
    {
        return Logger.Level.FULL;
    }
}
```
```yaml
logging:
  level:
    # feign日志以什么级别监控哪个接口
    com.xialuo.cloud.service: debug
```

#### 7.feign的一些文章
- [OpenFeign与Ribbon源码分析总结与面试题](https://juejin.cn/post/6844904200841740302 "OpenFeign与Ribbon源码分析总结与面试题")
- [Feign实现原理](https://my.oschina.net/wangshuhui/blog/3075874 "Feign实现原理")
```html
总结
总到来说，Feign的源码实现的过程如下：

首先通过@EnableFeignCleints注解开启FeignCleint
根据Feign的规则实现接口，并加@FeignCleint注解
程序启动后，会进行包扫描，扫描所有的@ FeignCleint的注解的类，并将这些信息注入到ioc容器中。
当接口的方法被调用，通过jdk的代理，来生成具体的RequesTemplate
RequesTemplate在生成Request
Request交给Client去处理，其中Client可以是HttpUrlConnection、HttpClient也可以是Okhttp
最后Client被封装到LoadBalanceClient类，这个类结合类Ribbon做到了负载均衡。
```

# 10.hystrix
#### 1。是什么
```html
Hystrix是一个用于处理分布式系统的延迟和容错的开源库， 在分布式系统里,许多依赖不可避免的会调用失败,比如超时、异常等,
Hystrix能够保证在一个依赖出问题的情况下,`不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。`

“断路器"本身是一种开关装置, 当某个服务单元发生故障之后,通过断路器的故障监控(类似熔断保险丝) .向调用方返回一个符合
预期的、可处理的备选响应(FallBack) .而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会
被长时间、不必要地占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。
```

#### 2。能干嘛
- 服务降级
- 服务熔断
- 接近实时的监控

#### 3.HyStrix重要概念
```html
服务降级
  服务器忙,请稍后再试,不让客户端等待并立刻返回一个友好提示,fallback
  哪些情况会发出降级
    程序运行异常
    超时
    服务熔断触发服务降级
    线程池/信号量也会导致服务降级
服务熔断
  类比保险丝达到最大服务访问后,直接拒绝访问,拉闸限电,然后调用服务降级的方法并返回友好提示
  就是保险丝
    服务的降级->进而熔断->恢复调用链路
服务限流
  秒杀高并发等操作,严禁一窝蜂的过来拥挤,大家排队,一秒钟N个,有序进行
```

#### 4.HyStrix解决的问题
```html
如何解决?解决的要求
  超时导致服务器变慢(转圈)
    超时不再等待
  出错(宕机或程序运行出错)
    出错要有兜底
  解决
    对方服务(8001)超时了,调用者(80)不能一直卡死等待,必须有服务降级
    对方服务(8001)down机了,调用者(80)不能一直卡死等待,必须有服务降级
    对方服务(8001)ok,调用者(80)自己有故障或有自我要求(自己的等待时间小于服务提供者)
```

#### 5。服务降级
##### 5.1。案例
```java
@RestController
@Slf4j
public class OrderHystirxController
{
/**
  * 描述: 上图故意制造两个异常: .
        1 int age = 10/0;计算异常
        2我们能接受1.5秒钟,它运行5秒钟，超时异常。
        当前服务不可用了，做服务降级，兜底的方案都是paymentTimeOutFallbackMethod.
  **/
    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1500")
    })
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id)
    {
//        int age = 10/0;
        String result = paymentHystrixService.paymentInfo_TimeOut(id);//会运行5秒
        return result;
    }
    public String paymentTimeOutFallbackMethod(@PathVariable("id") Integer id)
    {
        return "我是消费者80,对方支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,o(╥﹏╥)o";
    }
}
```
##### 5.2。 主启动 @EnableHystrix    
##### 5.3。 YML   
```yaml
feign:   
  hystrix:     
    enabled: true 
```       
##### 5.4.目前问题---->每个业务方法对应一个兜底的方法,代码膨胀,统一和自定义的分开
```java
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "payment_Global_FallbackMethod")
public class OrderHystirxController
{
    @Resource
    private PaymentHystrixService paymentHystrixService;

    /**
     * 描述:
     * 模拟如果调feign 8001宕机 返回 feign的fallback
     * 如果8001正常，但是超过我允许的1500毫秒 走80的全局fallback
     **/
    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    @HystrixCommand(commandProperties = {
        @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1500")
    })
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id)
    {
//        int age = 10/0;
        String result = paymentHystrixService.paymentInfo_TimeOut(id);
        return result;
    }

    // 下面是全局fallback方法
    public String payment_Global_FallbackMethod()
    {
        return "Global异常处理信息，请稍后再试，/(ㄒoㄒ)/~~";
    }
}
```
```java
@Component
@FeignClient(value = "CLOUD-PROVIDER-HYSTRIX-PAYMENT" ,fallback = PaymentFallbackService.class)
public interface PaymentHystrixService
{
    @GetMapping("/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id);
}
```
```java
@Component
public class PaymentFallbackService implements PaymentHystrixService
{
    @Override
    public String paymentInfo_TimeOut(Integer id)
    {
        return "-----PaymentFallbackService fall back-paymentInfo_TimeOut ,o(╥﹏╥)o";
    }
}
```

#### 6.服务熔断
##### 6.1。是什么
```html
熔断机制概述
熔断机制是应对零崩效应的一种微服务链路保护机制。 当扇出链路的某个微服务出错不可用或者响应时间太长时，
会进行服务的降级，进而熔断该节点微服务的调用，快速返回错误的响应信息。
当检测到该节点微服务调用响应正常后，恢复调用链路。
在Spring Cloud框架里，熔断机制通过Hystrix实现. Hystrix会监控微服务间调用的状况，
当失败的调用到一-定阈值，缺省是5秒内20次调用失败,就会启动熔断机制。熔断机制的注解是@HystrixCommand.
```

#### 6.2。案例
```java
@RestController
@Slf4j
public class PaymentController
{
 //=====服务熔断
    /**
     * 描述: 默认5秒内20次
     * 这个配置的意思是 时间窗口10秒中内，请求必须10次以上 如果小于10次 不会处罚熔断；
     * 大于10次以上请求，并且错误率在60%以上 会熔断；
     *
     * 当断路器打开，对主逻辑进行熔断之后，hystrix会启动一个休眠时间窗10秒, 在这个时间窗内，降级逻辑是临时的成为主逻辑,
     * 当休眠时间窗到期，断路器将进入半开状态,释放-次请求到原来的主逻辑上，如果此次请求正常返回,那么断路器将继续闭合,
     * 主逻辑恢复，如果这次请求依然有问题，断路器继续进入打开状态,休眠时间窗重新计时。
     *
     **/
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸 60%
    })
    public String paymentCircuitBreaker(@PathVariable("id") Integer id)
    {
        if(id < 0)
        {
            throw new RuntimeException("******id 不能负数");
        }
        String serialNumber = IdUtil.simpleUUID();

        return Thread.currentThread().getName()+"\t"+"调用成功，流水号: " + serialNumber;
    }
    public String paymentCircuitBreaker_fallback(@PathVariable("id") Integer id)
    {
        return "id 不能负数，请稍后再试，/(ㄒoㄒ)/~~   id: " +id;
    }
}
```

#### 6.3。熔断类型
```html
熔断打开
   请求不再调用当前服务,内部设置一般为MTTR(平均故障处理时间),当打开长达导所设时钟则进入半熔断状态
 熔断关闭
   熔断关闭后不会对服务进行熔断
 熔断半开
   部分请求根据规则调用当前服务,如果请求成功且符合规则则认为当前服务恢复正常,关闭熔断
```

#### 6.4.涉及到断路器的三个重要参数:快照时间窗、请求总数阀值、错误百分比阀值。
```html
1:快照时间窗:断路器确定是否打开需要统计-些请求和错误数据，而统计的时间范围就是快照时间窗，默认为最近的10秒。
2:请求总数阀值:在快照时间窗内，必须满足请求总数阀值才有资格熔断。默认为20,意味着在10秒内，如果该hystrix命令的调用次数不足20次，
即使所有的请求都超时或其他原因失败，断路器都不会打开。
3:错误百分比阀值:当请求总数在快照时间窗内超过了阀值，比如发生了30次调用，如果在这30次调用中,有15次发生了超时异常，也就是超过
50%的惜误百分比，在默认设定50%阀值情况下，这时候就会将断路器打开。
```

#### 6.5.服务监控hystrix-Dashboard
```html
除了隔离依赖服务的调用以外，Hystrix还提供 了准实时的调用监控(Hystrix Dashboard) , Hystrix会持续地记录所有通过Hystrix发
起的请求的执行信息，并以统计报表和图形的形式展示给用户，包括每秒执行多少请求多少成功，多少失败等。Netflix通过
hystrix- metrics-event-stream项目实现了对以上指标的监控。Spring Cloud也提供了Hystrix Dashboard的整合，对监控内容转化成
可视化界面。
```
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```
> 具体参考项目中的文档 《尚硅谷SpringCloud第2季2020.3.2.mmap》

# 11.gateway
#### 11.1简介 
> SpringCloud Gateway-句话: gateway是原zuul1.x版的替代

#### 11.2我们为什么选择Gateway?
##### 11.2.1netflix不太靠谱,zuul2.0一直跳票,迟迟不发布
```html
-方面因为Zuul1.0已经进入了维护阶段,而且Gateway是SpringCloud团队研发的, 是亲儿子产品，值得信赖。
而且很多功能Zuu|都没有用起来也非常的简单便捷。
Gateway是基于异步非阻塞模型上进行开发的，性能方面不需要担心。虽然Netflix早就发布了最新的Zuul 2.x,
旦Spring Cloud貌似没有整合计划。而且Netflix相关组件都宣布进入维护期;不知前景如何?
多方面综合考虑Gateway是很理想的网关选择。
```
##### 11.2.2SpringCloud Gateway具有如下特性
```html
基于Spring Framework 5, Project Reactor和Spring Boot 2.0进行构建;
动态路由:能够匹配任何请求属性;
可以对路由指定Predicate (断言)和Filter (过滤器) ;
集成Hystrix的断路器功能;
集成Spring Cloud服务发现功能;
易于编写的Predicate (断言滤器) ;
请求限流功能;
支持路径重写.
```

##### 11.2.3.SpringCloud Gateway与Zuul的区别
```html
Spring Cloud Gateway与Zuul的区别
在SpringCloud Finchley正式版之前，Spring Cloud推荐的网关是Netflix提供的Zuul:
1、Zuul1.x, 是一个基于阻塞I/ 0的API Gateway
2、 Zuul 1.x基于Servlet 2. 5使用阻塞架构它不支持任何长连接(如WebSocket) Zuul的设计模式和Nginx较像,每次I/O
工作线程中选择一个执行，请求线程被阻塞到工作线程完成，但是差别是Nginx用C++实现，Zuul用Java实现，而JVM
-次加载较慢的情况，使得Zuul的性能相对较差。
3、 Zuul 2.x理念更先进， 想基于Netty非阻塞和支持长连接,但SpringCloud目前还没有整合。 Zuul 2.x的性能较Zuul 1.x
。在性能方面，根据官方提供的基准测试，Spring Cloud Gateway的RPS (每秒请求数)是Zuul的1. 6倍。
4、 Spring Cloud Gateway建立在Spring Framework 5、 Project Reactor和Spring Boot2之上， 使用非阻塞API。
5、Spring Cloud Gateway还支持WebSocket， 并且与Spring紧密集成拥有更好的开发体验
```
###### 11.2.3.1Zuul1.x模型:Servlet2.5阻塞
```html
Springcloud中所集成的Zuul版本,采用的是Tomcat容器， 使用的是传统的Servlet I0处理模型。
学过尚硅谷web中期课程都知道一个题目， Servlet的生 命周期?servlet由servlet container进行生命周期管理.
container启动时构造servlet对象并调用servlet init()进行初始化;
container运行时接受请求，并为每个请求分配一个线程 (-般从线程池中获取空闲线程) 然后调用service().
container关闭时调用servlet destory()销毁servlet;

上述模式的缺点:
servlet是一个简单的网络I0模型,当请求进入servlet container时, servlet container就会为其绑定一个线程,在并发不高的场景下这种模型是适用
的。但是-旦高并发(比如抽风用jemeter压), 线程数量就会上涨，而线程资源代价是昂贵的(上线文切换，内存消耗大)严重影响请求的处理时间.
在一些简单业务场景下，不希望为每个request分配一个线程，只需要1个或几个线程就能应对极大并发的请求，这种业务场景下servlet模型没有优势
所以Zuul 1.X是基于servlet之上的一个阻塞式处理模型，即spring实现了处理所有request请求的一 个servlet (DispatcherServlet) 并由该servlet阻
塞式处理处理。所以Springcloud Zul无法摆脱servlet模型的弊端
```
##### 11.2.3.2Gateway模型:Servlet3.1异步非阻塞
```html
传统的Web框架,此如说: struts2, springmvc等都是基于Servlet API与Servlet容器基础之上运行的。
但是
在Servlet3.1之后有了异步非阻塞的支持。而WebFlux是一个典型非阻塞异步的框架，它的核心是基于Reactor的相关API实现的。相对
于传统的web框架来说， 它可以运行在诸如Netty, Undertow及支持Servlet3.1的容器上。非阻塞式+函数式编程(Spring5必须让你使
用java8)
Spring WebFlux是Spring 5.0引入的新的响应式框架，区别于Spring MVC,它不需要依赖Servlet API,它是完全异步非阻塞的，并
且基于Reactor来实现响应式流规范。
```
#### 11.3gateway三大核心概念
```html
1Route(路由)
路由是构建网关的基本模块,它由ID,目标URI,一系列的断言和过滤器组成,如断言为true则匹配该路由
2Predicate(断言)
参考的是Java8的java.util.function.Predicate 开发人员可以匹配HTTP请求中的所有内容(例如请求头或请求参数),如果请求与断言相匹配则进行路由
1.After Route Predicate 
2.Before Route Predicate 
3.Between Route Predicate 
4.Cookie Route Predicate 
5.Header Route Predicate 
6.Host Route Predicate
7.Method Route Predicate 
8.Path Route Predicate
9.Query Route Predicate 
10.RemoteAddr Route Predicate
11.Weight Route Predicate
3Filter(过滤)
指的是Spring框架中GatewayFilter的实例,使用过滤器,可以在请求被路由前或者之后对请求进行修改.
```

#### 11.4案例cloud-gateway-gateway9527
##### 11.4.1yml
```yaml
spring:
application:
name: cloud-gateway
cloud:
gateway:
discovery:
  locator:
    enabled: true #开启从注册中心动态创建路由的功能，利用微服务名进行路由
routes:
  - id: payment_routh #payment_route    #路由的ID，没有固定规则但要求唯一，建议配合服务名
#          uri: http://localhost:8001          #匹配后提供服务的路由地址
    uri: lb://cloud-payment-service #匹配后提供服务的路由地址
    predicates:
      - Path=/payment/get/**         # 断言，路径相匹配的进行路由

  - id: payment_routh2 #payment_route    #路由的ID，没有固定规则但要求唯一，建议配合服务名
#          uri: http://localhost:8001          #匹配后提供服务的路由地址
    uri: lb://cloud-payment-service #匹配后提供服务的路由地址
    predicates:
      - Path=/payment/lb/**         # 断言，路径相匹配的进行路由
#            - After=2020-12-23T22:51:37.485+08:00[Asia/Shanghai]
#            - Before=2020-12-23T23:51:37.485+08:00[Asia/Shanghai]
#            - Between=2020-12-23T22:51:37.485+08:00[Asia/Shanghai], 2020-12-23T23:01:37.485+08:00[Asia/Shanghai]
#            - Cookie=username,zzyy
#            - Header=X-Request-Id, \d+  # 请求头要有X-Request-Id属性并且值为整数的正则表达式
#            - Host=**.somehost.org,**.anotherhost.org
#            - Method=GET,POST
#            - Query=green
#            - RemoteAddr=192.168.1.1/24
#            - Weight=group1, 8
#            - Weight=group1, 2
#        - id: payment_routh3 #payment_route    #路由的ID，没有固定规则但要求唯一，建议配合服务名
#          uri: https://news.baidu.com      #匹配后提供服务的路由地址
##          uri: lb://cloud-payment-service #匹配后提供服务的路由地址
#          predicates:
#            - Path=/guonei/**         # 断言，路径相匹配的进行路由
#        #访问地址http://localhost:9527/guonei 会跳转->https://news.baidu.com/guonei
#        #他的意思是是 我访问网关9527的guonei路径 然后一看 网关里断言有国内匹配，然后找到uri是https://news.baidu.com
#        #最后跳转https://news.baidu.com/guonei
```
> 他的意思是是 我访问网关9527的guonei路径 然后一看 网关里断言有国内匹配，然后找到uri是https://news.baidu.com,
> 最后跳转https://news.baidu.com/guonei

##### 11.4.2config
```java
@Configuration
public class GateWayConfig
{
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder)
{
  RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();

  routes.route("path_route_atguigu",
          r -> r.path("/guonei")
                  .uri("http://news.baidu.com")).build();

  return routes.build();
}
}
```
##### 11.4.3自定义过滤器)和Filter (过GlobalFilter
```java
@Component
@Slf4j
public class MyLogGateWayFilter implements GlobalFilter,Ordered
{

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        log.info("***********come in MyLogGateWayFilter:  "+new Date());
        String uname = exchange.getRequest().getQueryParams().getFirst("uname");
        if(uname == null)
        {
            log.info("*******用户名为null，非法用户，o(╥﹏╥)o");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder()
    {
        return 0;
    }
}

```

# 12.SpringCloud config分布式配置中心
#### 12.1是什么、怎么玩
```html
SpringCloud Config为微服务架构中的微服务提供集中化的外部配置支持，配置服务器为各个不同微服务应用的所有环境提供了-个中
心化的外部配置。
```
```html
SpringCloud Config分为服务端和客户端两部分。
服务端也称为分布式配置中心，它是一个独立的微服务应用， 用来连接配置服务器并为客户端提供获取配置信息,加密/解密信息等访问接口
客户端则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容,并在启动的时候从配置中心获取和加载配置信息配置服
务器默认采用git来存储配置信息，这样就有助于对环境配置进行版本管理，并且可以通过git客户端工具来方便的管理和访问配置内容
```

#### 12.2 config服务端yaml:cloud-config-center-3344
```yaml
server:
  port: 3344

spring:
  application:
    name:  cloud-config-center #注册进Eureka服务器的微服务名
  cloud:
    config:
      server:
        git:
          uri: https://github.com/781616300/springcloud-config.git #GitHub上面的git仓库名字
          force-pull: true
          username: 781616300@qq.com
          password: 这个不能告诉你
        ####搜索目录
          search-paths:
            - springcloud-config
      ####读取分支
      label: main

#服务注册到eureka地址
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka
```
> 然后访问：http://localhost:3344/main/config-dev.yaml 就能出现配置的内容

#### 12.3 config客户端yaml:cloud-config-client-3355
```yaml
server:
  port: 3355

spring:
  application:
    name: config-client
  cloud:
    #Config客户端配置
    config:
      label: main #分支名称
      name: config #配置文件名称
      profile: dev #读取后缀名称   上述3个综合：master分支上config-dev.yml的配置文件被读取http://localhost:3344/main/config-dev.yaml
      uri: http://localhost:3344 #配置中心地址k

#服务注册到eureka地址
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka
# 暴露监控端点
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

#### 12.4动态刷新
```html
1.@refreshScope业务类Controller修改
2.运维需要执行post请求 手动刷新 curl -X POST "http://localhost:3355/actuator/refresh"
```

#### 12.5弊端：每个微服务都要执行一次post请求，手动刷新

# 13SpringCloud Bus消息总线：Spring Cloud Bus配合Spring Cloud Config使用可以实现配置的动态刷新
#### 13.1是什么
```html
在微服务架构的系统中，通常会使用轻量级的消息代理来构建一个共用的消息主题, 并让系统中所有微服务实例都连接上来。由于该主题中产生的消
息会被所有实例监听和消费，所以称它为消息总线。在总线上的各个实例，都可以方便地广播-些需要让其他连接在该主题上的实例都知道的消息。
Bus支持两种消息代理:RabbitMQ和Kafka
```
#### 13.2基本原理
```html
ConfigClient实例都监听MQ中同-个topic(默认是springCloudBus)。当个服务刷新数据的时候，它会把这个信息放入到Topic中, 这样其它监听
同-Topic的服务就能得到通知，然后去更新自身的配置。
```
#### 13.3设计思想
```html
微服务架构如下
7001EurekaServer
3344ConfigServer 注册在7001。配置从远程git仓库读取
3355ConfigClient 注册在7001。配置从3344上拉取
3366ConfigClient 注册在7001。配置从3344上拉取
3377ConfigClient 注册在7001。配置从3344上拉取

两种设计思量
1.利用消息总线触发一个客户端/bus/refresh，从而刷新所有客户端配置
2.利用消息总线触发一个服务端ConfigServer的/bus/refresh端点，从而刷新所有客户端配置

2的架构显然更加合适，图一不合适原因如下：
a.打破了微服务的职责单一性，因为微服务本身是业务模块，它本不应该承担配置刷新的职责
b.破坏了微服务各节点的对等性
c.有一定的局限性，例如，微服务在迁移时，它的网络地址常常会发生变化，此时如果想要做到自动刷新那就会增加更多的修改
```

#### 13.4测试
````html
1.修改Github上配置文件增加版本号
2.发送Post请求 curl -X POST "http://localhost:3344/actuator/bus-refresh" 一次发送，处处生效
3.如果想要 3355 3366 生效 3377不生效 curl -X POST "http://localhoste:3344/actuator/bus-rfresh/config-client:3355"
````

# 15SpringCloud Stream消息驱动
#### 15.1什么是SpringCloudStream
```html
官方定义Spring Cloud Stream是一个构建消息驱动微服务的框架。
应用程序通过inputs或者outputs来与Spring Cloud Stream中binder对象交互。
通过我们配置来binding(绑定)，而Spring Cloud Stream的binder对象负责与消息中间件交互。
所以，我们只需要搞清楚如何与Spring Cloud Stream交互就可以方便使用消息驱动的方式。
通过使用Spring Integration来连接消息代理中间件以实现消息事件驱动。
Spring Cloud Stream为-些供应商的消息中间件产品提供了个性化的自动化配置实现，引了发布订阅、消费组、分区的三个核心概念。
目前仅支持RabbitMQ、Kafka.


一句话:屏蔽底层消息中间件的差异，降低切换成本，统一消息的编程模型
```

#### 15.2stream凭什么可以统一底层差异
```html
在没有绑定器这个概念的情况下，我们的SpringBoot应用要 直接与消息中间件进行信息交互的时候,
由于各消息中间件构建的初衷不同，它们的实现细节上会有较大的差异性
通过定义绑定器作为中间层，完美地实现了应用程序与消息中间件细节之间的隔离。
通过向应用程序暴露统一的Channel通道, 使得应用程序不需要再考虑各种不同的消息中间件实现.
通过定义绑定器Binder作为中间层，实现了应用程序与消息中间件细节之间的隔离。

Stream中的消息通信方式遵循了发布-订阅模式
Topic主题进行广播
在RabbitMQ就是Exchange
在Kafka中就是Topic
```

#### 15.3Spring Cloud Stream标准流程套路
```html
1.Binder:很方便的连接中间件，屏蔽差异
2.Channel:通道，是队列Queue的一种抽象，在消息通讯系统中就是实现存储和转发的媒介，通过Channel对队列进行配置
3.Source和Sink:简单的可以理解为参照对象是Spring Cloud Stream 自身，从Stream发布消息就是输出，接受消息就是输入

消息生产者                                 消息消费者
业务逻辑                                   业务逻辑 
  ⬇                                         ⬆
springcloud stream                        springcloud stream
source                                    sink
channel                                   channel
binder ➡➡➡➡➡➡➡➡➡➡➡➡➡➡➡➡MQ➡➡➡➡➡➡➡➡➡➡➡➡➡➡➡➡ binder
```

#### 15.4分组：为了不重复消费
#### 15.5持久化：配置分组就会持久化

# 16SpringCloud Sleuth分布式链路跟踪
> Spring Cloud Sleuth提供了一套完整的服务跟踪的解决方案，在分布式系统中提供追踪解决方案并且兼容支持了zipkin

# 17SpringCloud Alibaba Nacos服务注册和配置中心
#### 17.1是什么
```html
Nacos就是注册中心+配置中心的组合 等价于 Nacos=Eureka+Config+Bus
Nacos支持AP和CP模式的切换

DataID方案
Group方案
Namespace方案
```

#### 17.2Nacos集群和持久化配置(重要)
```html
1.Nacos默认自带的是嵌入式数据库derby
2.执行数据库nacos-server-1.1.4\nacos\conf目录下找到sql脚本\nacos-mysql.sql
3。修改nacos-server-1.1.4\nacos\conf目录下找到application.properties
```

#### 17.3Linux版Nacos+MySQL生产环境配置
```html
预计需要，1个nginx+3个nacos注册中心，1个mysql

nginx配置 
所有请求1111端口的都走nginx路由
转发到 3333 4444 5555端口

nacos配置
3.Linux服务器上nacos的集群配置cluster.conf
192.168.111.144:3333
192.168.111.144:4444
192.168.111.144:5555

4.编辑Nacos的启动脚本startup.sh,使他能够接受不同的启动端口 上网搜就能搜到
/mynacos/nacos/bin 目录下有startup.sh

最后验证
http://192.168.111.144:1111/nacos/#/login
访问1111的话 nginx转发到 3333 4444 5555  实现集群

最后业务中配置的 
spring:
  application:
    name: nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.111.144:1111 #配置Nacos地址
```

# 19 SpringCloud Alibaba Sentinel实现熔断与限流

#### 19.1流控模式

- 关联
```html
是什么
当关联的资源达到阈值时，就限流自己
当与A关联的资源B达到阈值后，就限流自己
B惹事，A挂了
```
- 链路:多个请求调用同一个微服务

- 直接拒绝（RuleConstant.CONTROL_BEHAVIOR_DEFAULT）方式是默认的流量控制方式，
```html
当QPS超过任意规则的阈值后，新的请求就会被立即拒绝，拒绝方式为抛出FlowException。
```

- Warm Up（RuleConstant.CONTROL_BEHAVIOR_WARM_UP）方式，即预热/冷启动方式。
```html
当系统长期处于低水位的情况下，当流量突然增加时，直接把系统拉升到高水位可能瞬间把系统压垮。
通过"冷启动"，让通过的流量缓慢增加，在一定时间内逐渐增加到阈值上限，给冷系统一个预热的时间，避免冷系统被压垮。
令牌桶算法
我们用桶里剩余的令牌来量化系统的使用率。假设系统每秒的处理能力为 b,系统每处理一个请求，就从桶中取走一个令牌；
每秒这个令牌桶会自动掉落b个令牌。令牌桶越满，则说明系统的利用率越低；当令牌桶里的令牌高于某个阈值之后，我们称之为令牌桶"饱和"。
公式:阈值除以coldFactor(默认值为3)，经过预热时长后才会达到阈值
说明：默认coldFactor为3，即请求QPS从threshold/3开始，经预热时长逐渐升至设定的QPS阈值
```

- 匀速排队（RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER）方式会严格控制请求通过的间隔时间，也即是让请求以均匀的速度通过，对应的是漏桶算法。
com.alibaba.csp.sentinel.slots.block.flow.controller.DefaultController 
com.alibaba.csp.sentinel.slots.block.flow.controller.RateLimiterController 
com.alibaba.csp.sentinel.slots.block.flow.controller.WarmUpController 
com.alibaba.csp.sentinel.slots.block.flow.controller.WarmUpRateLimiterController

#### 19.2降级规则
- 慢调用比例 (SLOW_REQUEST_RATIO)(秒级)：选择以慢调用比例作为阈值，需要设置允许的慢调用 RT（即最大的响应时间），请求的响应时间大于该值则统计为慢调用。当单位统计时长（statIntervalMs）内请求数目大于设置的最小请求数目，并且慢调用的比例大于阈值，则接下来的熔断时长内请求会自动被熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求响应时间小于设置的慢调用 RT 则结束熔断，若大于设置的慢调用 RT 则会再次被熔断。
```html
RT (平均响应时间，秒级)
平均响应时间超出阈值且在时间窗口内通过的请求> =5,两个条件同时满足后触发降级
窗口期过后关闭断路器
RT最大4900 (更大的需要通过-Dcsp.sentinel.statistic.max.rt=XXXX才能生效)
```
- 异常比例 (ERROR_RATIO)(秒级)：当单位统计时长（statIntervalMs）内请求数目大于设置的最小请求数目，并且异常的比例大于阈值，则接下来的熔断时长内请求会自动被熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。异常比率的阈值范围是 [0.0, 1.0]，代表 0% - 100%。
```html
异常比例( DEGRADE GRADE EXCEPTION RATIO):当資源的毎秒清求量>= 5,并且毎秒异常息数占
通过量的比値超过阈值( DegradeRule中的count )之后,炎源迸入降級状志，即在接下的吋
间窗口( DegradeRule中的timeWindow, 以sカ単位)之内，対送个方法的凋用都会自幼地返
回。昇常比率的閾値范国是[0.0, 1.0] ,代表0%- 100%。
```
- 异常数 (ERROR_COUNT)(分钟)：当单位统计时长内的异常数目超过阈值之后会自动进行熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。
```html
异常数( DEGRADE _GRADE _EXCEPTION _COUNT ):当资源近1分钟的异常数目超过阈值之后会进行熔
断。注意由于统计时间窗口是分钟级别的，若timelWindow 小于60s,则结束熔断状态后仍可能
再进入熔断状态。
```

#### 19.3热点key限流
- HystrixCommand 到 @SentinelResource
```html
第一个参数p1，当QPS超过1秒1次点击后马上被限流

普通
超过1秒钟一个后，达到阈值1后马上被限流
我们期望p1参数当它是某个特殊值时，它的限流值和平时不一样
特例
假如当p1的值等于5时，它的阈值可以达到200
```

#### 19.4系统规则（不是重点）
```
系统保护规则是从应用级别的入口流量进行控制，从单台机器的load. CPU 使用率.平均RT,入口
QPS和并发线程数等几个维度监控应用指标，让系统尽可能跑在最大吞吐量的同时保证系统整体的稳定性。
系统保护规则是应用整体维度的，而不是资源维度的，并且仅对入口流量生效。入口流量指的是进入
应用的流量( EntryType.IN) 。比如Web服务或Dubbo服务端接收的请求，都属于入口流量。
```

#### 19.5@SentinelResource
- 注解地址：https://github.com/alibaba/Sentinel/wiki/%E6%B3%A8%E8%A7%A3%E6%94%AF%E6%8C%81
```java
//自定义限流
@SentinelResource(value = "customerBlockHandler",
        blockHandlerClass = CustomerBlockHandler.class,
        blockHandler = "handlerException2")

//blockHandlerClass 限流类
//blockHandler      限流的方法
```

- Sentinel主要有三个核心Api
  - sphU定义资源
  - Tracer定义统计
  - ContextUtil定义了上下文

#### 19.6服务熔断功能:sentinel整合ribbon+openFeign+fallback
```java
//cloudalibaba-provider-payment9003
//cloudalibaba-provider-payment9004
//cloudalibaba-consumer-nacos-order84

@RestController
@Slf4j
public class CircleBreakerController
{
   public static final String SERVICE_URL = "http://nacos-payment-provider";

   @Resource
   private RestTemplate restTemplate;

   @RequestMapping("/consumer/fallback/{id}")
//    @SentinelResource(value = "fallback") //没有配置
//    @SentinelResource(value = "fallback",fallback = "handlerFallback") //fallback只负责业务异常
//    @SentinelResource(value = "fallback",blockHandler = "blockHandler") //blockHandler只负责sentinel控制台配置违规
   @SentinelResource(value = "fallback",fallback = "handlerFallback",blockHandler = "blockHandler",
           exceptionsToIgnore = {IllegalArgumentException.class})
   public CommonResult<Payment> fallback(@PathVariable Long id)
   {
       CommonResult<Payment> result = restTemplate.getForObject(SERVICE_URL + "/paymentSQL/"+id,CommonResult.class,id);

       if (id == 4) {
           throw new IllegalArgumentException ("IllegalArgumentException,非法参数异常....");
       }else if (result.getData() == null) {
           throw new NullPointerException ("NullPointerException,该ID没有对应记录,空指针异常");
       }

       return result;
   }
   //本例是fallback
   public CommonResult handlerFallback(@PathVariable  Long id,Throwable e) {
       Payment payment = new Payment(id,"null");
       return new CommonResult<>(444,"兜底异常handlerFallback,exception内容  "+e.getMessage(),payment);
   }
   //本例是blockHandler
   public CommonResult blockHandler(@PathVariable  Long id,BlockException blockException) {
       Payment payment = new Payment(id,"null");
       return new CommonResult<>(445,"blockHandler-sentinel限流,无此流水: blockException  "+blockException.getMessage(),payment);
   }

   //==================OpenFeign
   @Resource
   private PaymentService paymentService;

   @GetMapping(value = "/consumer/paymentSQL/{id}")
   @SentinelResource(value = "fallback",fallback = "handlerFallback",blockHandler = "blockHandler",
       exceptionsToIgnore = {IllegalArgumentException.class})
   public CommonResult<Payment> paymentSQL(@PathVariable("id") Long id)
   {
       return paymentService.paymentSQL(id);
   }
}
```

- 解释

- @SentinelResource(value = "fallback") //没有配置
- @SentinelResource(value = "fallback",fallback = "handlerFallback") //fallback只负责业务异常
- @SentinelResource(value = "fallback",blockHandler = "blockHandler") //blockHandler只负责sentinel控制台配置违规
- @SentinelResource(value = "fallback",fallback = "handlerFallback",blockHandler = "blockHandler",
           exceptionsToIgnore = {IllegalArgumentException.class})
           
@FeignClient(value = "nacos-payment-provider",fallback = PaymentFallbackService.class)

1。sentinelResource 的注解 
- value 是sentinel监控平台上的资源名  
- fallback是降级 兜底方法，不过fallback只负责业务异常
- blockHandler是降级 兜底方法，不过blockHandler只负责sentinel控制台配置违规

2。@FeignClient 的注解
- fallback是调用的时候 如果服务宕机不通的降级

比如通信用的是ribbon 的RestTemplate 的话 如果 调用对方宕机，会走SentinelResource fallback的兜底；如果没配置 则异常
比如通信用的是feign 的话 如果 调用对方宕机，会走@FeignClient fallback的兜底


#### 19.7 持久化：必须写到nacos里边 否者配置不能持久化，发版一次就没了

# 20.SpringCloud Alibaba Seata处理分布式事务
#### 20.1是什么:Seata是一款开源的分布式事务解决方案,致力于在微服务架构下提供高性能和简单易用的分布式事务服务
#### 20.2组成:
```html
分布式事务处理过程 一个ID+三组件模型
(1)Transaction ID XID：全局唯一的事务ID

(2)三组概念：

    ①Transaction Coordinator（TC事务协调器）：维护全局和分支事务状态，驱动全局事务提交或回滚

    ②Transaction Manager（TM事务管理器）：控制全局事务的边界，负责开启一个全局事务，并最终发起全局提交或全局回滚的决议

    ③Resource Manager（RM资源管理器）：控制分支事务,负责分支注册、状态汇报,并接受事务协调的指令,驱动分支(本地)事务的提交和回滚
```

#### 20.3 Seata处理过程
```html
①TM向TC申请开启一个全局事务，全局事务创建成功并生成一个全局唯一的XID；

②XID在微服务调用链路的上下文传播

③RM向TC注册分支事务，将其纳入XID对应全局事务的管辖

④TM向TC发起针对XID的全局提交或回滚决议；

⑤TC调度XID下管辖的全部分支事务完成提交或回滚事务
```

#### 20.4Seata-Server安装
```html
1。下载seata-server-0.9.0.zip
2。修改file.conf 
31行：  vgroup_mapping.my_test_tx_group = "fsp_tx_group"
57行：  mode = "db"
82-84行：    url = "jdbc:mysql://127.0.0.1:3306/seata"
           user = "root"
           password = "123456"
3。修改registry.conf
3行：  type = "nacos"
6行：    serverAddr = "59.110.213.162:8848"

```

#### 20.5数据库初始化
```sql
①seata_order：存储订单数据库；

②seata_storage：存储库存数据；

③seata_account：存储账户信息数据库；

---seata biz
create database seata_order;
USE seata_order;
CREATE TABLE `t_order`  (
  `int` bigint(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `product_id` bigint(11) DEFAULT NULL COMMENT '产品id',
  `count` int(11) DEFAULT NULL COMMENT '数量',
  `money` decimal(11, 0) DEFAULT NULL COMMENT '金额',
  `status` int(1) DEFAULT NULL COMMENT '订单状态:  0:创建中 1:已完结',
  PRIMARY KEY (`int`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

create database seata_storage;
USE seata_storage;
DROP TABLE IF EXISTS `t_storage`;
CREATE TABLE `t_storage`  (
  `int` bigint(11) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(11) DEFAULT NULL COMMENT '产品id',
  `total` int(11) DEFAULT NULL COMMENT '总库存',
  `used` int(11) DEFAULT NULL COMMENT '已用库存',
  `residue` int(11) DEFAULT NULL COMMENT '剩余库存',
  PRIMARY KEY (`int`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '库存' ROW_FORMAT = Dynamic;
INSERT INTO `t_storage` VALUES (1, 1, 100, 0, 100);

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE database seata_account;
USE seata_account;
DROP TABLE IF EXISTS `t_account`;
CREATE TABLE `t_account`  (
  `id` bigint(11) NOT NULL COMMENT 'id',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
  `total` decimal(10, 0) DEFAULT NULL COMMENT '总额度',
  `used` decimal(10, 0) DEFAULT NULL COMMENT '已用余额',
  `residue` decimal(10, 0) DEFAULT NULL COMMENT '剩余可用额度',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '账户表' ROW_FORMAT = Dynamic;

INSERT INTO `t_account` VALUES (1, 1, 1000, 0, 1000);

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

```html
1、建立对应3个数据库与对应的回滚日志表
（回滚日志表对应建表语句：/seata/conf/db_undo_log.sql）
2、2001服务为订单服务驱动业务
  2002服务为库存服务
  2003服务为账户服务

2.1正常情况测试
2.2不使用@GlobalTransactional注解超时异常测试
2.3使用@GlobalTransactional注解


发现使用@GlobalTransactional注解后，数据库记录进行了回滚。实现了分布式事务
```

#### 20.6Seata原理简介
```html
(1)一阶段加载
　　在一阶段，Seata会拦截“业务SQL”：

　　①解析SQL语义，找到“业务SQL”要更新的业务数据，在业务数据被更新之前，将其保存成“before image”

　　②执行“业务SQL”更新业务数据，在业务数据更新之后，将其生成“after image”

　　③生成行锁

以上操作全部在一个数据库事务内完成，这样保证了一阶段操作的原子性。

(2)二阶段提交
二阶段如果是顺利的话，因为“业务SQL”在一阶段已经提交至数据库，所以Seata框架只需将一阶段保存的快照数据和行锁删掉，完成数据清理即可。

(3)二阶段回滚
　　二阶段如果回滚的话，Seata就需要回滚一阶段已执行的“业务SQL”，还原业务数据

　　回滚方式便是用“before image”还原业务数据；但在还原之前首先要校验脏写，对比“数据当前业务数据”和“after image”，如果两份数据完全一致就说明没有脏写，可以还原业务数据，如果不一致就说明脏鞋，需要人工处理。
```





