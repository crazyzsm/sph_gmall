package com.arzhu.sph_gmall.product.controller;

import com.arzhu.sph_gmall.common.result.Result;
import com.arzhu.sph_gmall.product.service.TestRedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: TestControllerController
 * Package: com.arzhu.sph_gmall.product.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/20 14:59
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product/test")
public class TestRedisLockController {
    @Autowired
    private TestRedisLockService testRedisLockService;

    @GetMapping("/redisLock")
     public Result testRedisLock(){
        testRedisLockService.testRedisLock();
        return Result.ok();
    }
}
