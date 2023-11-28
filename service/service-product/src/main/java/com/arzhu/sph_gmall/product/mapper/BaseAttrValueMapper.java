package com.arzhu.sph_gmall.product.mapper;

import com.arzhu.sph_gmall.model.product.BaseAttrInfo;
import com.arzhu.sph_gmall.model.product.BaseAttrValue;
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
public interface BaseAttrValueMapper extends BaseMapper<BaseAttrValue> {
}
