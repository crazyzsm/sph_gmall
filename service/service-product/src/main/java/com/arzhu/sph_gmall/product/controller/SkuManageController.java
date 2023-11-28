package com.arzhu.sph_gmall.product.controller;

import com.arzhu.sph_gmall.common.result.Result;
import com.arzhu.sph_gmall.model.product.SkuInfo;
import com.arzhu.sph_gmall.product.service.BaseSaleAttrService;
import com.arzhu.sph_gmall.product.service.SkuManageService;
import com.arzhu.sph_gmall.product.service.SpuManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: SkuManageController
 * Package: com.arzhu.sph_gmall.product.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 20:01
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
public class SkuManageController {
    @Autowired
    private SpuManageService spuManageService;

    @Autowired
    private SkuManageService skuManageService;

    @GetMapping("/spuImageList/{spuInfoId}")
    public Result findSpuImageList(@PathVariable Integer spuInfoId){

        return Result.ok(spuManageService.findSpuImageList(spuInfoId));
    }
    @GetMapping("/spuSaleAttrList/{spuInfoId}")
    public  Result spuSaleAttrList(@PathVariable Integer spuInfoId){
        return Result.ok(spuManageService.findSpuSaleAttrList(spuInfoId));
    }
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuManageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @GetMapping("/list/{pageNo}/{pageSize}")
    public  Result findPageSkuList(@PathVariable Integer pageNo,@PathVariable Integer pageSize){
        Page<SkuInfo> page = new Page<>(pageNo,pageSize);
        IPage<SkuInfo> pageSkuList = skuManageService.findPageSkuList(page);
        return Result.ok(pageSkuList);
    }

    /**
     * 上架处理
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{sku_id}")
    public Result onSale(@PathVariable("sku_id") Integer skuId){
        skuManageService.onSale(skuId);
        return Result.ok();
    }
    /**
     * 上架处理
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{sku_id}")
    public Result cancelSale(@PathVariable("sku_id") Integer skuId){
        skuManageService.cancelSale(skuId);
        return Result.ok();
    }
}
