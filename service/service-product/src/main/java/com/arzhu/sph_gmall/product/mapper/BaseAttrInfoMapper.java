package com.arzhu.sph_gmall.product.mapper;

import com.arzhu.sph_gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: BaseAttrInfoMapper
 * Package: com.arzhu.sph_gmall.product.mapper
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/13 22:47
 * @Version 1.0
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    /**
     * 根据 分类查询所有的 属性表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> selectAttrInfoList(@Param("category1Id") Integer category1Id,
                                          @Param("category2Id") Integer category2Id,
                                          @Param("category3Id") Integer category3Id);

    /**
     * 查找 sku 对应的属性
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getAttrListBySkuId(@Param("skuId") Long skuId);
}
