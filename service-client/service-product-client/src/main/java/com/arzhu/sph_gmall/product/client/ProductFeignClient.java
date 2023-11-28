package com.arzhu.sph_gmall.product.client;

import com.arzhu.sph_gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ProductFeignClient
 * Package: com.arzhu.sph_gmall.product.client
 * Description:
 *      value ----->  是 service-product 的 接口 要是使用这个 bean 那么 代表了 service-product 的接口
 *      fallback -----> 代表着服务降级，假如 service-product 出现问题，那么就调用降级方法
 * @Author arzhu
 * @Create 2023/11/18 11:17
 * @Version 1.0
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {
    /**
     * 根据id 查找 sku 信息
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getAttrValueList(@PathVariable Long skuId);

    /**
     * 根据 分类3 id 返回 分类1、分类2 等
     * @param category3Id
     * @return
     */

    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id);

    /**
     * 返回 实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId);

    /**
     * 返回 SpuSaleAttrValue 销售数据 其中通过 字段 isChecked  判断是否 选中状态
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId, @PathVariable Long spuId);

    /**
     * 根据 spuId 返回一个 “属性值1|属性值2:skuId” 的 json 字符串
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId);

    /**
     * 返回一个 对应的 spu 海报
     *  海报是 spu 公用的 sku是自己用的一个
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable("spuId") Long spuId);
    /**
     * 返回 sku 对应的 属性值和属性信息
     */
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    public List<BaseAttrInfo>  getAttrList(@PathVariable("skuId") Long skuId);
}
