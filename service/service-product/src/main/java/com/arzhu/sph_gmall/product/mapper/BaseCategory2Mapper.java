package com.arzhu.sph_gmall.product.mapper;

import com.arzhu.sph_gmall.model.product.BaseCategory1;
import com.arzhu.sph_gmall.model.product.BaseCategory2;
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
public interface BaseCategory2Mapper extends BaseMapper<BaseCategory2> {
    /**
     * 根据 分类1 的id 查找 分类2
     * @param category1Id
     * @return
     */
    List selectByCategory1Id(@Param("category1Id") Integer category1Id);
}
