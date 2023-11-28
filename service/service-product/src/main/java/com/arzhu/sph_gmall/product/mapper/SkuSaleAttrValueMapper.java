package com.arzhu.sph_gmall.product.mapper;

import com.arzhu.sph_gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * ClassName: SkuSaleAttrValueMapper
 * Package: com.arzhu.sph_gmall.product.mapper
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 21:32
 * @Version 1.0
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 返回一个 属性A|属性B : skuId 的map
     * @param spuId
     * @return
     */
    List<Map> selectSaleAttrValuesBySpu(Long spuId);
}
