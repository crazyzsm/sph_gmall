package com.arzhu.sph_gmall.product.controller;

import com.arzhu.sph_gmall.model.product.*;
import com.arzhu.sph_gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ProductApiController
 * Package: com.arzhu.sph_gmall.product.controller
 * Description:
 *  本控制器 是为了 给 内部服务提供 数据支撑的
 * @Author arzhu
 * @Create 2023/11/16 16:29
 * @Version 1.0
 */
@RestController
@RequestMapping("api/product/inner")
public class ProductApiController {
    @Autowired
    private SkuManageService skuManageService;
    @Autowired
    private SpuSaleAttrService spuSaleAttrService;

    @Autowired
    private SpuManageService spuManageService;

    @Autowired
    private BaseAttrInoService baseAttrInoService;


    /**
     * 根据id 查找 sku 信息
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getAttrValueList(@PathVariable Long skuId){
        SkuInfo skuInfo = skuManageService.getSkuInfo(skuId);
        return skuInfo;
    }

    /**
     * 根据 分类3 id 返回 分类1、分类2 等
     * @param category3Id
     * @return
     */

    @GetMapping("/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id){
        BaseCategoryView categoryView = skuManageService.getCategoryView(category3Id);
        return categoryView;
    }

    /**
     * 返回 实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId){
        System.out.println("进入斤斤计较");
       return skuManageService.getSkuPrice(skuId);
    }

    /**
     * 返回 SpuSaleAttrValue 销售数据 其中通过 字段 isChecked  判断是否 选中状态
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId, @PathVariable Long spuId){
       return   spuSaleAttrService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }

    /**
     * 根据 spuId 返回一个 “属性值1|属性值2:skuId” 的 json 字符串
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        return skuManageService.getSkuValueIdsMap(spuId);
    }

    /**
     * 返回一个 对应的 spu 海报
     *  海报是 spu 公用的 sku是自己用的一个
     * @param spuId
     * @return
     */
    @GetMapping("/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable("spuId") Long spuId){
        return spuManageService.findSpuPosterBySpuId(spuId);
    }
    /**
     * 返回 sku 对应的 属性值和属性信息
     */
    @GetMapping("/getAttrList/{skuId}")
    public List<BaseAttrInfo>  getAttrList(@PathVariable("skuId") Long skuId){
        return baseAttrInoService.getAttrListBySkuId(skuId);
    }

}
