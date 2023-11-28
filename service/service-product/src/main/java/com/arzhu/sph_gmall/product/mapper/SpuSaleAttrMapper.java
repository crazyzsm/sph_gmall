package com.arzhu.sph_gmall.product.mapper;

import com.arzhu.sph_gmall.model.product.SpuSaleAttr;
import com.arzhu.sph_gmall.model.product.SpuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * ClassName: SpuSaleAttrMapper
 * Package: com.arzhu.sph_gmall.product.mapper
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 16:33
 * @Version 1.0
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@Param("skuId") Long skuId, @Param("spuId") Long spuId);

}
