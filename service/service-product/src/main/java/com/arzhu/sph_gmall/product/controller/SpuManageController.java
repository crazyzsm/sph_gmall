package com.arzhu.sph_gmall.product.controller;

import com.arzhu.sph_gmall.common.result.Result;
import com.arzhu.sph_gmall.model.product.BaseSaleAttr;
import com.arzhu.sph_gmall.model.product.SpuInfo;
import com.arzhu.sph_gmall.product.service.BaseSaleAttrService;
import com.arzhu.sph_gmall.product.service.SpuManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: SpuInfoController
 * Package: com.arzhu.sph_gmall.product.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 13:50
 * @Version 1.0
 */
@Api(tags = "商品SKU接口")
@RestController
@RequestMapping("/admin/product")
public class SpuManageController {
    @Autowired
    private SpuManageService spuManageService;

    @Autowired
    private BaseSaleAttrService baseSaleAttrService;


    @GetMapping("/{pageNo}/{pageSize}")
    public Result getSPUList(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize,
                             SpuInfo spuInfo){
        Page<SpuInfo> page = new Page<>(pageNo,pageSize);
        IPage<SpuInfo> spuInfoPage = spuManageService.getSpuInfoPage(page, spuInfo);
        return Result.ok(spuInfoPage);
    }

    /**
     *
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    @ApiOperation("获取所有的销售属性")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> list = baseSaleAttrService.getAllList();
        return Result.ok(list);
    }

    @PostMapping("/saveSpuInfo")
    @ApiOperation("添加新的SPU")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuManageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

//    @GetMapping("/spuImageList/{spuInfoId}")
//    public  Result findSpuImageList(Integer spuInfoId){
//
//        return Result.ok(spuManageService.findSpuImageList(spuInfoId));
//    }
//    @GetMapping("/spuSaleAttrList/{spuInfoId}")
//    public  Result spuSaleAttrList(Integer spuInfoId){
//
//        return Result.ok(spuManageService.findSpuSaleAttrList(spuInfoId));
//    }
}
