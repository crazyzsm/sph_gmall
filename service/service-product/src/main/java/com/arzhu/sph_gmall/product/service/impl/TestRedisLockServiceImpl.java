package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.product.controller.TestRedisLockController;
import com.arzhu.sph_gmall.product.service.TestRedisLockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: TestRedisLockServiceImpl
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/20 16:24
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestRedisLockServiceImpl implements TestRedisLockService {
    /**
     * 测试 和实现 分布式锁
     * 1、通过 setNX 等 添加一个 键为 lock 值为 UUID 的 键 作为抢夺资源的锁标志，为了防止 死锁，所以需要设置过期时间
     * 2、获得了锁的 执行 自己的操作，执行完毕释放锁
     * 3、没有获得锁的 自旋，自旋尝试次数 超过三次，那么就进入 sleep
     * 4、释放锁 需要执行的步骤为：①、比较当前锁的UUID 是否是自身的ID ②、是的话，那么就删除 所以需要 lua 脚本保证原子性
     */
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * redisson 实现 分布式锁
     * @return
     */

    @Override
    public Void testRedisLock() {
        // 创建锁：
        String skuId="25";
        String locKey ="lock:"+skuId;
        // 锁的是每个商品
        RLock rLock = redissonClient.getLock(locKey);
        rLock.lock(3, TimeUnit.SECONDS);  // 3秒之后就会过期
//        执行业务逻辑
        // 获取数据
        String value = redisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(value)){
            return null;
        }
        redisTemplate.opsForValue().increment("num",1);
//        解锁
        rLock.unlock();
        return null;
    }

//    @Override
//    public Void testRedisLock() {
//// 1. 从redis中获取锁,set k1 v1 px 20000 nx
//        String uuid = UUID.randomUUID().toString();
//        Boolean lock = this.redisTemplate.opsForValue()
//                .setIfAbsent("lock", uuid, 2, TimeUnit.SECONDS);
//        if(lock){
////            得到锁 进行 共享资源的操作
//            String num = redisTemplate.opsForValue().get("num");
//            if(StringUtils.isEmpty(num)){
////                为空 那么就算了
//            }else{
//
//                redisTemplate.opsForValue().increment("num",1);
//            }
//// 2. 释放锁 del
//            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//// 设置lua脚本返回的数据类型
//            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//// 设置lua脚本返回类型为Long
//            redisScript.setResultType(Long.class);
//            redisScript.setScriptText(script);
//            redisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);
//
//        }else{
////            否则的话。那就自旋
//            try {
//                Thread.sleep(500);
//                testRedisLock();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }


}
