package com.arzhu.sph_gmall.product.controller;

import com.arzhu.sph_gmall.common.result.Result;
import com.arzhu.sph_gmall.model.product.BaseTrademark;
import com.arzhu.sph_gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: BaseTrademarkController
 * Package: com.arzhu.sph_gmall.product.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/14 22:33
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @GetMapping("/{pageNo}/{pageSize}")
    @ApiOperation(value = "分页列表")
    public Result baseTrademark(@PathVariable Integer pageNo, @PathVariable Integer pageSize){
        Page<BaseTrademark> pageParam  = new Page<>(pageNo,pageSize);
        IPage<BaseTrademark> page = baseTrademarkService.getPage(pageParam);
        return Result.ok(page);
    }
    @PostMapping("/save")
    public  Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }
    @GetMapping("/get/{trademarkId}")
    public Result get(@PathVariable Integer trademarkId){
        return Result.ok(baseTrademarkService.getById(trademarkId));
    }

    @PutMapping ("/update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }
    @DeleteMapping ("/remove/{trademarkId}")
    public Result remove(@PathVariable Integer trademarkId){
        baseTrademarkService.removeById(trademarkId);
        return Result.ok();
    }
}
