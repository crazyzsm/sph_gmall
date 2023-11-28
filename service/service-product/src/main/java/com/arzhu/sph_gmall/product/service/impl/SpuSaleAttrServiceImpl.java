package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.common.cache.GmallCache;
import com.arzhu.sph_gmall.model.product.SpuSaleAttr;
import com.arzhu.sph_gmall.product.mapper.SpuSaleAttrMapper;
import com.arzhu.sph_gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: SpuSaleAttrValueServiceImpl
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/16 17:27
 * @Version 1.0
 */
@Service
public class SpuSaleAttrServiceImpl implements SpuSaleAttrService {
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Override
    @GmallCache(prefix = "spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }

}
