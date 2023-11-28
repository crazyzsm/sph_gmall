package com.arzhu.sph_gmall.product.service;

import com.arzhu.sph_gmall.model.product.BaseCategoryView;
import com.arzhu.sph_gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * ClassName: SkuManageService
 * Package: com.arzhu.sph_gmall.product.service
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 21:00
 * @Version 1.0
 */
public interface SkuManageService extends IService<SkuInfo> {
    /**
     * 创建一个 新的 sku
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 分页查询
     * @param page
     * @return
     */

    IPage<SkuInfo> findPageSkuList(Page<SkuInfo> page);

    /**
     * 上架
     * @param skuId
     */
    void onSale(Integer skuId);

    /**
     * 下架处理
     * @param skuId
     */
    void cancelSale(Integer skuId);

    /**
     * 根据 skuId 查找 skuInfo 的相关信息
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据 分类三的id 查询 分类二和分类一
     * 这里返回的是一个 视图
     * 视图缺点有点多，所以要谨慎使用
     * @param category3Id
     * @return
     */
    BaseCategoryView getCategoryView(Long category3Id);

    /**
     * 返回实时价格
     * sku 的其他信息就可以 存储在 mysql 价格必须存储在 数据库中实时查询
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(Long skuId);
    /**
     * 根据 spuId 返回一个 “属性值1|属性值2:skuId” 的 json 字符串
     * @param spuId
     * @return
     */
    Map getSkuValueIdsMap(Long spuId);
}
