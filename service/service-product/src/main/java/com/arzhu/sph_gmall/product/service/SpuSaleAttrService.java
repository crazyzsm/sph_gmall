package com.arzhu.sph_gmall.product.service;

import com.arzhu.sph_gmall.model.product.SpuSaleAttr;

import java.util.List;

/**
 * ClassName: SpuSaleAttrValueService
 * Package: com.arzhu.sph_gmall.product.service
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/16 17:27
 * @Version 1.0
 */
public interface SpuSaleAttrService {
    /**
     * 通过 skuId 和 spuId 返回 SpuSaleAttrValue 表 其中 is_checked 代表是否选中的状态
     *
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

}
