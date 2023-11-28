# 尚品汇 技术汇总
## 11/18 openFeign调用
在一个 springCloud 项目中，服务A要调用服务B，那么流程为：
1、服务A 和 服务B 引入 openFeign 依赖
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- 服务调用feign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <scope>provided </scope>
        </dependency>
```
2、服务A 需要在启动类中 指明 feign 所在的包

```java
@EnableFeignClients(basePackages= {"com.arzhu.sph_gmall"})  // 将扫描 com.arzhu.sph_gmall 包下的 feign
```
3、服务B中同样需要在启动类中 指明 feign 所在的包
```java
@EnableFeignClients(basePackages= {"com.arzhu.sph_gmall"})  // 将扫描 com.arzhu.sph_gmall 包下的 feign
```
4、服务B要暴露给其他服务的接口，需要创建一个单独的接口进行配置

```java
@FeignClient(value = "service-product")
public interface ProductFeignClient {
    /**
     * 根据id 查找 sku 信息
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getAttrValueList(@PathVariable Long skuId);
}
```
5、服务A service层 直接注入
```java
    @Autowired
    private ProductFeignClient productFeignClient;
```
那么自此以来调用链路为：A.controller ---> A.service ---> B.feign ---> B.controller ---> B.service

**但是为了 更好的扩展性，我们一般如下做**
1. 将Feign 从 A、B 中抽取成 AFeign、BFeign 模块
2. AFeign、BFeign 模块 是一个jar 包
3. A调用B的Feign 那么就直接调用，其他服务要想调用 Feign 也是直接调用

所以此时 模块分成：

+ service = A+B
+ service-feign = AFeign +  BFeign

注意：

+ <font color="red">**要使用 `Feign`调用别的服务的服务，需要添加 `@EnableFeignClients(basePackages= {"com.arzhu.sph_gmall"})`**</font>
+ <font color="red">**还要添加 `BFeign` 的依赖，不然找不到B提供的`Feign`接口**</font>

这里要是看不明白，那么就去看`/api/item` 接口就行。

梳理如下：

```
模块关系：
Servie = service-item + service-product
ServieClient = (service-item-client) + (service-product-client)
思考过程：
service-item-client 需要注明 service-item 暴露给外部服务的接口，使用 @FeignClient(value = "service-item")  value 指的是 需要为哪个服务提供 对外暴露的接口，也就是 被别人调用的服务名
service-product-client 需要注明 service-product 暴露给外部服务的接口，使用 @FeignClient(value = "service-product")  value 指的是 需要为哪个服务提供 对外暴露的接口，也就是 被别人调用的服务名

service-item 需要调用 service-product 的服务
既然要调用的话，那么 就需要去调用 service-product-client
那么 service-item 需要集成 service-product-client 的依赖咯
然后 service-item 的启动类需要添加
@EnableFeignClients(basePackages= {"com.arzhu.sph_gmall"}) ，因为有了 service-product-client 的依赖，那么，就能通过 包名找到，于是，我们通过 这个指定了包名的注解，就能直接使用`@Autowired` 直接使用 service-product 的接口了
```

## 11/21 商品详情优化

商品的部分信息是很少会变更的，所以这部分数据我们存储在 redis 中 减轻 mysql 的压力

对于部分实时性要求比较高的 数据，我们选择直接从数据库获取

对于商品详情页来说，

+ **价格是敏感，对于价格实时性要求高，所以我们必须要从数据库中获取**
+ **其他信息可以从redis 中获取**

### 如何使用 redis？

1. **引入依赖**，在本项目中，在 `service-util` 集成了，只要依赖`service-util`即可

   ```xml
           <!-- redis -->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-data-redis</artifactId>
           </dependency>
   ```

2. 可以**配置** redis，`service-util` 下就有

   ```java
   package com.arzhu.sph_gmall.common.config;
   
   import com.fasterxml.jackson.annotation.JsonAutoDetect;
   import com.fasterxml.jackson.annotation.PropertyAccessor;
   import com.fasterxml.jackson.databind.ObjectMapper;
   import org.springframework.cache.CacheManager;
   import org.springframework.cache.annotation.EnableCaching;
   import org.springframework.cache.interceptor.KeyGenerator;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.data.redis.cache.RedisCacheConfiguration;
   import org.springframework.data.redis.cache.RedisCacheManager;
   import org.springframework.data.redis.connection.RedisConnectionFactory;
   import org.springframework.data.redis.core.RedisTemplate;
   import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
   import org.springframework.data.redis.serializer.RedisSerializationContext;
   import org.springframework.data.redis.serializer.RedisSerializer;
   import org.springframework.data.redis.serializer.StringRedisSerializer;
   
   import java.lang.reflect.Method;
   import java.time.Duration;
   
   /**
    * Redis配置类 
    	@Configuration + Bean 就能 自动注入 RedisTemplate
    */
   @Configuration
   @EnableCaching
   public class RedisConfig {
       // 声明模板
       /*
       ref = 表示引用
       value = 具体的值
       <bean class="org.springframework.data.redis.core.RedisTemplate" >
           <property name="defaultSerializer" ref = "">
       </bean>
        */
       @Bean
       public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
           RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
           //  设置redis的连接池工厂。
           redisTemplate.setConnectionFactory(redisConnectionFactory);
           //  设置序列化的。
           Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
           ObjectMapper objectMapper = new ObjectMapper();
           objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
           objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
           jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
           //  将Redis 中 string ，hash 数据类型，自动序列化！
           redisTemplate.setKeySerializer(new StringRedisSerializer());
           redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
           //  设置数据类型是Hash 的 序列化！
           redisTemplate.setHashKeySerializer(new StringRedisSerializer());
           redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
   
           redisTemplate.afterPropertiesSet();
           return redisTemplate;
       }
   
   }
   
   ```

   上方的 配置 主要是，针对 redis 的序列化的，简单来说，就是 使 我们存入 redis 的 字符串 跟我们直接去查看 redis 的一样，不然会有乱码的情况

3. **使用**

   ```java
       @Autowired
       private RedisTemplate<String,String> redisTemplate;
   ```

   具体API 可以去查一下

### redis 实现分布式锁

1. 通过 setNX 等 添加一个 键为 lock 值为 UUID 的 键 作为抢夺资源的锁标志，为了防止 死锁，所以需要设置过期时间，为了保证原子性，我们使用 redis 提供的 api 进行操作

2. 获得了锁的 执行 自己的操作，执行完毕释放锁

3. 没有获得锁的 自旋

4. 释放锁 需要执行的步骤为：

   1. 比较当前锁的UUID 是否是自身的ID 
   2. 是的话，那么就删除

    所以**需要 lua 脚本保证原子性**

5. <font color="red">拓展内容</font>：要是想 锁的 机制 更加健壮，那么，我们可以在这基础上实现：

   + **可重入功能**：类似于`synchronized` 的轻量级锁概念，我们可以设置一个计数器，当一个线程获取这个锁时，让他的计数器+1，但是，解锁的时候，也要计数器-1，当计数器为0时，就代表 真正解锁
   + **续约功能**：当一个线程 正常执行，但是 过期时间到了之后，我们还需要将这个锁的时间延期，可以起一个定时任务执行。

以下是简单的 redis 分布式锁的实现

```java

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Void testRedisLock() {
        String uuid = UUID.randomUUID().toString();
//        过期时间为 3 s，这里设置了 UUID 和 过期时间
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        if(lock){
//            得到锁 进行 共享资源的操作
            String num = redisTemplate.opsForValue().get("num");
            if(StringUtils.isEmpty(num)){
//                为空 那么就算了
            }else{

                redisTemplate.opsForValue().increment("num",1);
            }
//            解锁 要保证原子性：1、比对 2、删除 操作
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            /**
             * redisScript  --- 执行的脚本对象
             * Arrays.asList("lock") --- 锁的key KEYS[1]
             * uuid  --- 传入 lua 脚本的 参数 ARGV[1]
             */
            redisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
        }else{
//            否则的话。那就自旋
            try {
                Thread.sleep(500);
                testRedisLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
```

### redisson 实现分布式锁

可以看到 使用 redis 实现分布式锁，还是比较麻烦，所以，我们 推荐使用 redisson 实现

Github 地址：https://github.com/redisson/redisson

#### 使用 redisson 

1. 引入依赖，在本项目中，已经在 `service-util`已经引入了

   ```xml
   <!-- redisson -->
   <dependency>
      <groupId>org.redisson</groupId>
      <artifactId>redisson</artifactId>
      <version>3.15.3</version>
   </dependency>
   
   ```

2. **配置**，读取了 `spring.redis` 中的配置

   ```java
   /**
    * redisson配置信息
    */
   @Data
   @Configuration
   @ConfigurationProperties("spring.redis")
   public class RedissonConfig {
   
       private String host;
   
       private String addresses;
   
       private String password;
   
       private String port;
   
       private int timeout = 3000;
       private int connectionPoolSize = 64;
       private int connectionMinimumIdleSize=10;
       private int pingConnectionInterval = 60000;
       private static String ADDRESS_PREFIX = "redis://";
   
       /**
        * 自动装配
        *
        */
       @Bean
       RedissonClient redissonSingle() {
           Config config = new Config();
           if(StringUtils.isEmpty(host)){
               throw new RuntimeException("host is  empty");
           }
           SingleServerConfig serverConfig = config.useSingleServer()
                   //redis://127.0.0.1:7181
                   .setAddress(ADDRESS_PREFIX + this.host + ":" + port)
                   .setTimeout(this.timeout)
                   .setPingConnectionInterval(pingConnectionInterval)
                   .setConnectionPoolSize(this.connectionPoolSize)
                   .setConnectionMinimumIdleSize(this.connectionMinimumIdleSize)
                   ;
           if(!StringUtils.isEmpty(this.password)) {
               serverConfig.setPassword(this.password);
           }
           ent redisson = Redisson.create(config);
           return Redisson.create(config);
       }
   }
   ```

3. **使用**

   ```java
   @Autowired
   private RedissonClient redissonClient;
   
   ```


#### 实现分布式锁

```java
        // 创建锁：
        String skuId="25";
        String locKey ="lock:"+skuId;
        // 锁的是每个商品
        RLock lock = redissonClient.getLock(locKey);
//      尝试获取锁 获取锁的尝试时间为 1s 锁的过期时间为 3s
        boolean res = rLock.tryLock(1, 3, TimeUnit.SECONDS)

            if(res){
                //        获取锁成功 执行业务逻辑
            }else{
                // 获取锁失败 自旋
            }
//        解锁
        rLock.unlock();
```

#### 实现读写锁

```java
RReadWriteLock rwlock = redisson.getReadWriteLock("anyRWLock");
// 最常见的使用方法
rwlock.readLock().lock();
// 或
rwlock.writeLock().lock();

// 10秒钟以后自动解锁
// 无需调用unlock方法手动解锁
rwlock.readLock().lock(10, TimeUnit.SECONDS);
// 或
rwlock.writeLock().lock(10, TimeUnit.SECONDS);

// 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
boolean res = rwlock.readLock().tryLock(100, 10, TimeUnit.SECONDS);
// 或
boolean res = rwlock.writeLock().tryLock(100, 10, TimeUnit.SECONDS);
...
lock.unlock();

```



## 11/23 aop应用分布式锁

我们要自己编写一个 aop 应用。

最佳的应用实践，应该是像`@Transactional` 事务一样，即：<font color="red">**自定义一个注解，编写一个针对于这个注解的处理类，当有对象用这个注解，就会被 aop 实现 公共逻辑！！！**</font>

步骤如下：

#### 1、编写 注解 

```java
package com.arzhu.sph_gmall.common.cache;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * ClassName: GmallCache
 * Package: com.arzhu.sph_gmall.common.cache
 * Description:
 * 分布式缓存 的 注解
 *  key 前缀 和 后缀 搭配 方法的 参数 生成 redis 的key
 *  例如：   @GmallCache(prefix = "sku",suffix="info")
 *          getSkuInfo(331)
 *          那么生成的 redis 的key 为 sku:331:info
 * @Author arzhu
 * @Create 2023/11/22 21:50
 * @Version 1.0
 */
// 代表着 只能在 方法上起作用
@Target({ElementType.METHOD})
/**
 * 代表着声明周期   可以填的值如下：
 *  1、RetentionPolicy.SOURCE：注解只保留在源文件，当 从 .java 编译成 .class 时 就消失了
 *  2、RetentionPolicy.CLASS：注解被保留到 class 文件中，但是 jvm 编译时 就消失 这是默认的
 *  3、RetentionPolicy.RUNTIME：注解不仅被保存到class文件中，在 jvm 编译之后仍然存在
 */
@Retention(RetentionPolicy.RUNTIME)
/**
 * 继承 是否有效
 * 当添加了 这个注解 那么就代表 子类也会拥有这个注解
 * 比如：class A{} class B extends A {} 假如A 有 这个注解，那么 B也有
 */
@Inherited
// 代表 使用 javadoc 编译时 能显示出 具体的值
@Documented
public @interface GmallCache {
//    key 的前缀
    String prefix() default "key";
//    key的后缀
    String suffix() default "info";
}

```

重点为：

+ `@Target`：指明当前注解的应用对象
+ `@Retention`：指明 注解的生命周期
+ `属性`：如上面的`prefix` 一样，这样子 别的 对象使用注解时，就能 进行传值

#### 2、编写处理类

```java
@Component  // 需要被收集到 IOC 中 这样才能用
@Aspect   // 代表 这是一个 注解处理类
public class GmallCaheAspect {
    /**
     * 这里使用 环绕通知 的目的是
     * 可以修改 返回值
     * point 代表了 使用了 @GmallCache 注解的对象
     * @return
     */
    @Around("@annotation(com.arzhu.sph_gmall.common.cache.GmallCache)")
    public Object cacheGmallAspect(ProceedingJoinPoint point){
        Object obj = new Object();
//        1、先获取到参数
        Object[] args = point.getArgs(); // 获取参数对象 使用 Arrays.asList(args) 转换成 list 进行处理
//        2、获取这个方法的 注解 的参数
        MethodSignature methodSignature = (MethodSignature) point.getSignature();  // 得到这个方法
        Method method = methodSignature.getMethod();
        GmallCache annotation = method.getAnnotation(GmallCache.class);  // 得到注解
        Arrays.asList(args).toString();  // 这就是参数列表了
        return obj;
    }
}
```

重点为：

+ `@Component`：代表将被 IOC 容器管理，但是！！！  <font color="red">**要保证，这个处理类要能被扫描到，不然一切就白搭了**</font>
+ `@Aspect`：代表是一个 切面类
+ `@Around("@annotation(com.arzhu.sph_gmall.common.cache.GmallCache)")`：使用的是一个 环绕通知，针对于 `GmallCache` 这个注解，也就是只要用了 这个 注解的 对象 就会被我们 AOP 管理，然后执行具体的逻辑

#### 3、使用

由于 Spring 中 的 AOP 的特殊机制，所以要想使用  aop 我们要保证： <font color="red">**使用切面类的方法，必须不能在这个类内部调用！！！**</font>

SpringAOP 的机制为：

+ 当有接口时，使用`JDK` 的动态代理，去继承这个接口
+ 当没有接口时，那么就使用`cglib` 去代理

如果你的方法是在内部调用的话，就没有地方去 代理这个方法。

举例：

**失败的**

```java
class A {
    public void test1(){
        test2();
    }
    @GmallCache
    public void test2(){
        
    }
}
```

可以看到 `test2` 使用了 `aop`，但是它在 `test1`中 内部调用，所以 <font color="red">**aop 不生效**</font>

**正确的**

```java
class A {
    @GmallCache
    public void test1(){
        test2();
    }
   
    public void test2(){
        
    }
}
```

## 11/27 通过 aop + 分布式锁 集成通用的redis缓存功能

思路是这样子的：

+ 功能：对于冷数据，对实时性要求不高的数据，我们从 redis 中读取，那么，可以将 这种 功能 使用 aop 实现，由于 多个线程去操作 可能会涉及到线程安全，所以我们要加上分布式锁

+ 具体实现方案：

  1. 定义一个注解，当我们使用该注解时，就能自动 使用 该功能

     ```java
     package com.arzhu.sph_gmall.common.cache;
     
     import org.springframework.core.annotation.AliasFor;
     
     import java.lang.annotation.*;
     
     /**
      * ClassName: GmallCache
      * Package: com.arzhu.sph_gmall.common.cache
      * Description:
      * 分布式缓存 的 注解
      *  key 前缀 和 后缀 搭配 方法的 参数 生成 redis 的key
      *  例如：   @GmallCache(prefix = "sku",suffix="info")
      *          getSkuInfo(331)
      *          那么生成的 redis 的key 为 sku:331:info
      * @Author arzhu
      * @Create 2023/11/22 21:50
      * @Version 1.0
      */
     // 代表着 只能在 方法上起作用
     @Target({ElementType.METHOD})
     /**
      * 代表着声明周期   可以填的值如下：
      *  1、RetentionPolicy.SOURCE：注解只保留在源文件，当 从 .java 编译成 .class 时 就消失了
      *  2、RetentionPolicy.CLASS：注解被保留到 class 文件中，但是 jvm 编译时 就消失 这是默认的
      *  3、RetentionPolicy.RUNTIME：注解不仅被保存到class文件中，在 jvm 编译之后仍然存在
      */
     @Retention(RetentionPolicy.RUNTIME)
     /**
      * 继承 是否有效
      * 当添加了 这个注解 那么就代表 子类也会拥有这个注解
      * 比如：class A{} class B extends A {} 假如A 有 这个注解，那么 B也有
      */
     @Inherited
     // 代表 使用 javadoc 编译时 能显示出 具体的值
     @Documented
     public @interface GmallCache {
     //    key 的前缀
         String prefix() default "key";
     //    key的后缀
         String suffix() default "info";
     }
     
     ```

  2. 定义 注解的处理类

     ```java
     package com.arzhu.sph_gmall.common.cache;
     
     import com.alibaba.fastjson.JSON;
     import com.arzhu.sph_gmall.common.constant.RedisConst;
     import org.aspectj.lang.ProceedingJoinPoint;
     import org.aspectj.lang.Signature;
     import org.aspectj.lang.annotation.Around;
     import org.aspectj.lang.annotation.Aspect;
     import org.aspectj.lang.reflect.MethodSignature;
     import org.redisson.Redisson;
     import org.redisson.api.RLock;
     import org.redisson.api.RedissonClient;
     import org.springframework.beans.factory.annotation.Autowired;
     import org.springframework.data.redis.core.RedisTemplate;
     import org.springframework.stereotype.Component;
     import org.springframework.util.StringUtils;
     
     import java.lang.reflect.Method;
     import java.util.Arrays;
     import java.util.concurrent.TimeUnit;
     
     /**
      * ClassName: GmallCaheAspect
      * Package: com.arzhu.sph_gmall.common.cache
      * Description:
      *  GmallCache 的 处理类
      *  主要作用是 实现一个 分布式锁 将 应用了 @GmallCache 的方法 的返回值存在 redis 并且 解决 缓存穿透 的问题
      * @Author arzhu
      * @Create 2023/11/22 22:04
      * @Version 1.0
      */
     @Component  // 需要被收集到 IOC 中 这样才能用
     @Aspect   // 代表 这是一个 注解处理类
     public class GmallCaheAspect {
         @Autowired
         private RedissonClient redissonClient;
         @Autowired
         private RedisTemplate<String,String> redisTemplate;
         /**
          * 这里使用 环绕通知 的目的是
          * 可以修改 返回值
          * point 代表了 使用了 @GmallCache 注解的对象
          * @return
          */
         @Around("@annotation(com.arzhu.sph_gmall.common.cache.GmallCache)")
         public Object cacheGmallAspect(ProceedingJoinPoint point) throws Throwable {
     
         }
     
     }
     
     ```
     + **@Component**：需要被 IOC 管理
     + **@Aspect**：代表这是一个注解处理类
     + **`@Around("@annotation(com.arzhu.sph_gmall.common.cache.GmallCache)")`**：环绕通知，针对于 `GmallCache` 注解 。

  3. 注解内部处理类的实现：

     1. 通过`ProceedingJoinPoint对象`获取到 注解的参数列表
     2. 通过这个参数 构建 redis 的 key
     3. 从 redis 中通过  key 获取数据
     4. 获取到了，那么就返回
     5. 获取不到 那么就调用数据库，此时，就是分布式锁的问题了，使用 `Redisson`实现
     6. 获取到了锁：
        + 执行 方法从数据库查询数据
        + 数据存在：存储在 redis 中
        + 数据不存在：构建一个 全新的 空对象，存储在 redis 中，防止第二次及以后的缓存击穿问题
     7. 没获取到锁：睡眠几秒后自旋

     具体实现如下：

     ```java
     @Around("@annotation(com.arzhu.sph_gmall.common.cache.GmallCache)")
         public Object cacheGmallAspect(ProceedingJoinPoint point) throws Throwable {
             Object obj = new Object();
     //        1、先获取到参数
             Object[] args = point.getArgs(); // 获取参数对象 使用 Arrays.asList(args) 转换成 list 进行处理
     //        2、获取这个方法的 注解 的参数
             MethodSignature methodSignature = (MethodSignature) point.getSignature();  // 得到这个方法
             Method method = methodSignature.getMethod();
             GmallCache annotation = method.getAnnotation(GmallCache.class);  // 得到注解
             System.out.println(Arrays.asList(args));
     //        3、组装得到 存入 redis 的key
             String key = annotation.prefix()+ Arrays.asList(args).toString()+annotation.suffix();
     //        4、接下来是走 分布式锁的流程
     //        4.1 从 redis 获取数据
             obj = cacheHit(key, methodSignature);
             try {
     //            4.2 判断 是否存在
                 if(obj==null){
     //                4.2.1 不存在的话 那么 就需要 从数据库查询，然后存入 redis 如果是 数据库也没有的，那就做一个空的 redis 防止 缓存穿透
     //                4.2.2 获取 分布式锁
     //                4.2.3 得到 分布式锁的 key
                     String lockKey = annotation.prefix()+ Arrays.asList(args).toString()+ RedisConst.SKULOCK_SUFFIX;
                     RLock rLock = redissonClient.getLock(lockKey);
                     boolean flag = rLock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                     if(flag){
                         try {
     //                    获取到锁 从数据库中获取 数据 写入 redis
                             obj = point.proceed(args);
                             if(obj==null){
     //                      没有查询到  需要 缓存一个 为 null 的 防止缓存穿透
                                 Class aClass = methodSignature.getReturnType();
                                 obj = aClass.newInstance();
                             }
                             redisTemplate.opsForValue().set(key, JSON.toJSONString(obj), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                             return obj;
                         } finally {
                             rLock.unlock();
                         }
                     }else{
     //                    没获取到锁 进行自旋
                         Thread.sleep(1000);
                         cacheGmallAspect(point);
                     }
     
                 }else{
                     return obj;
                 }
             }catch (Exception e){
                 e.printStackTrace();
             }
             return point.proceed(args);
         }
     ```

     使用：

     ```java
         @Override
         @GmallCache(prefix = "sku")
         public SkuInfo getSkuInfo(Long skuId) {
             //     return getSkuInfoRedis(skuId);
     //        return getSkuInfoRedisson(skuId);
           return getSkuInfoDB(skuId);
         }
     ```

     