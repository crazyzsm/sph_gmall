package com.arzhu.sph_gmall.item.client;

import com.arzhu.sph_gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName: ItemFeignClient
 * Package: com.arzhu.sph_gmall.item.client
 * Description:
 *      value ----->  提供 service-item 服务的Feign 接口
 * @Author arzhu
 * @Create 2023/11/18 13:15
 * @Version 1.0
 */
@FeignClient(value = "service-item")
public interface ItemFeignClient {
    @GetMapping("/api/item/{skuId}")
    Result getItem(@PathVariable Long skuId);
}
