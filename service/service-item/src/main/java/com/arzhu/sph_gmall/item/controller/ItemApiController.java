package com.arzhu.sph_gmall.item.controller;

import com.arzhu.sph_gmall.common.result.Result;
import com.arzhu.sph_gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: ItemController
 * Package: com.arzhu.sph_gmall.item.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/16 16:01
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/item")
public class ItemApiController {
    @Autowired
    private ItemService itemService;
    @GetMapping("/{skuId}")
    public Result getItem(@PathVariable Long skuId){
        Map<String, Object> skuInfo = itemService.getBySkuId(skuId);
        return Result.ok(skuInfo);
    }
}
