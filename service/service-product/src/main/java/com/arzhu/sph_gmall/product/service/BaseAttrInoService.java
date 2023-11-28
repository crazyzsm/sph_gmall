package com.arzhu.sph_gmall.product.service;

import com.arzhu.sph_gmall.model.product.BaseAttrInfo;

import java.util.List;

/**
 * ClassName: BaseAttrInoService
 * Package: com.arzhu.sph_gmall.product.service
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/16 22:15
 * @Version 1.0
 */
public interface BaseAttrInoService {
    /**
     * 根据 skuId 查找到对应的 属性和属性值
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getAttrListBySkuId(Long skuId);
}
