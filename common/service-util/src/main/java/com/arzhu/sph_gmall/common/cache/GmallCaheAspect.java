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

    /**
     * 从 redis 中获取 缓存
     * @param key
     * @param signature
     * @return
     */

    private Object cacheHit(String key, MethodSignature signature){
        String strJson = redisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(strJson)){
//            获取返回类型
            Class returnType = signature.getReturnType();
            return JSON.parseObject(strJson,returnType);
        }
        return null;
    }
}
