package com.arzhu.sph_gmall.product.service;

import com.arzhu.sph_gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName: ManagerService
 * Package: com.arzhu.sph_gmall.product.service
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/13 21:04
 * @Version 1.0
 */
public interface SpuManageService extends IService<SpuInfo> {
    /**
     * 根据 spuInfo 获取翻页接口
     * @param pageParam  翻页参数
     * @param spuInfo
     * @return
     */
    IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> pageParam,SpuInfo spuInfo);

     void saveSpuInfo(SpuInfo entity);

    List<SpuImage> findSpuImageList(Integer spuInfoId);

    /**
     * 查找 属性 和对应的属性值
     * @param spuInfoId
     * @return
     */
    List<SpuSaleAttr> findSpuSaleAttrList(Integer spuInfoId);

    /**
     * 根据 spuId 获取海报
     * @param spuId
     * @return
     */

    List<SpuPoster> findSpuPosterBySpuId(Long spuId);
}
