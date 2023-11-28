package com.arzhu.sph_gmall.product.controller;

import com.arzhu.sph_gmall.common.result.Result;
import com.arzhu.sph_gmall.model.product.BaseCategoryTrademark;
import com.arzhu.sph_gmall.model.product.CategoryTrademarkVo;
import com.arzhu.sph_gmall.product.service.BaseCategoryTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: BaseCategoryTrademarkController
 * Package: com.arzhu.sph_gmall.product.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/14 22:32
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product/baseCategoryTrademark")
public class BaseCategoryTrademarkController {
    @Autowired
    private BaseCategoryTrademarkService baseCategoryTrademarkService;
    @GetMapping("/findTrademarkList/{category3Id}")
    @ApiOperation(value = "分类品牌列表")
    public Result baseCategoryTrademark(@PathVariable Integer category3Id){

        return Result.ok(baseCategoryTrademarkService.findTrademarkByCategoryList(category3Id));
    }

    @GetMapping("/findCurrentTrademarkList/{category3Id}")
    public Result findCurrentTrademarkList(@PathVariable Integer category3Id){
        return Result.ok(baseCategoryTrademarkService.findCurrentTrademarkList(category3Id));
    }
    @PostMapping("/save")
    public Result save(@RequestBody CategoryTrademarkVo categoryTrademarkVo){
        baseCategoryTrademarkService.saveCategoryTrademark(categoryTrademarkVo);
        return Result.ok();
    }
    @DeleteMapping("/remove/{category3Id}/{trademarkId}")
    public Result remove(@PathVariable Integer category3Id,@PathVariable Integer trademarkId){
        baseCategoryTrademarkService.removeCategoryTrademark(category3Id,trademarkId);
        return Result.ok();
    }
}
