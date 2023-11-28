package com.arzhu.sph_gmall.all.controller;

import com.arzhu.sph_gmall.common.result.Result;
import com.arzhu.sph_gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * ClassName: ItemController
 * Package: com.arzhu.sph_gmall.all.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/18 13:54
 * @Version 1.0
 */
@Controller
public class ItemController {
    @Autowired
    private ItemFeignClient itemFeignClient;
    /**
     * sku详情页面
     * @param skuId
     * @param model
     * @return
     */
    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
        // 通过skuId 查询skuInfo
        Result<Map> result = itemFeignClient.getItem(skuId);
        model.addAllAttributes(result.getData());
        return "item/item";
    }

}
