package com.arzhu.sph_gmall.product.service;

import com.arzhu.sph_gmall.model.product.BaseCategoryTrademark;
import com.arzhu.sph_gmall.model.product.BaseTrademark;
import com.arzhu.sph_gmall.model.product.CategoryTrademarkVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName: BaseCategoryTrademarkService
 * Package: com.arzhu.sph_gmall.product.service
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/14 21:54
 * @Version 1.0
 */

public interface BaseCategoryTrademarkService extends IService<BaseCategoryTrademark> {
    /**
     * 根据三级分类ID 查找 品牌列表
     * @param category3
     * @return
     */
    List<BaseTrademark> findTrademarkByCategoryList(Integer category3);

    /**
     * 获取 当前没有被关联的品牌
     * @param category3Id
     * @return
     */
    List<BaseTrademark> findCurrentTrademarkList(Integer category3Id);

    /**
     * 新增 品牌跟分类列表
     * @param categoryTrademarkVo
     */
    void saveCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo);

    /**
     * 根据 三级分类ID 跟  trademarkId 删除 分类品牌表
     * @param category3Id
     * @param trademarkId
     */
    void removeCategoryTrademark(Integer category3Id, Integer trademarkId);
}
