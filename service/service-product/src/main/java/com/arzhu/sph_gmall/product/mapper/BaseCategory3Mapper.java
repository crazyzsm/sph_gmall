package com.arzhu.sph_gmall.product.mapper;

import com.arzhu.sph_gmall.model.product.BaseCategory1;
import com.arzhu.sph_gmall.model.product.BaseCategory3;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: BaseCategory1Mapper
 * Package: com.arzhu.sph_gmall.product.mapper
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/13 21:06
 * @Version 1.0
 */
@Mapper
public interface BaseCategory3Mapper extends BaseMapper<BaseCategory3> {
//    /**
//     * 根据 分类2 的id 查找 分类3
//     * @param category2Id
//     * @return
//     */
//    List selectByCategory2Id(@Param("category2Id") Integer category2Id);
}
