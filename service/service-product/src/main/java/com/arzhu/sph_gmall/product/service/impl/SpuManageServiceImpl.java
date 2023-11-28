package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.common.cache.GmallCache;
import com.arzhu.sph_gmall.model.product.*;
import com.arzhu.sph_gmall.product.mapper.*;
import com.arzhu.sph_gmall.product.service.SpuManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: ManageServiceImpl
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 14:47
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SpuManageServiceImpl extends ServiceImpl<SpuInfoMapper,SpuInfo> implements SpuManageService {
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;  // 图片表
    @Autowired
    private SpuPosterMapper spuPosterMapper; // 海报表
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper; // 销售属性值表
     @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper; // 销售属性值表
    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfo spuInfo) {
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",spuInfo.getCategory3Id());
        Page<SpuInfo> page = spuInfoMapper.selectPage(pageParam, wrapper);
        return page;
    }

    /**
     * 创建一个 spuInfo
     * 1、创建 spuInfo 表
     * 2、创建 图片表
     * 3、创建 海报表
     * 4、创建 销售属性表
     * 5、创建 销售属性值表
     * @param spuInfo
     * @return
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
//        1、创建 spuInfo 表
        this.save(spuInfo);
        final Long SPU_INFO_ID = spuInfo.getId();
//        2、创建 图片表
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(SPU_INFO_ID);
            spuImageMapper.insert(spuImage);
        }
//        3、创建 海报表
        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();
        for (SpuPoster spuPoster : spuPosterList) {
            spuPoster.setSpuId(SPU_INFO_ID);
            spuPosterMapper.insert(spuPoster);
        }
//        4、创建 销售属性表
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(SPU_INFO_ID);
            spuSaleAttrMapper.insert(spuSaleAttr);
            //        5、创建 销售属性值表
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(SPU_INFO_ID);
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }
        }
    }

    @Override
    public List<SpuImage> findSpuImageList(Integer spuInfoId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper();
        wrapper.eq("spu_id", spuInfoId);
        return spuImageMapper.selectList(wrapper);
    }

    @Override
    public List<SpuSaleAttr> findSpuSaleAttrList(Integer spuInfoId) {
        QueryWrapper<SpuSaleAttr> wrapper = new QueryWrapper();
        wrapper.eq("spu_id", spuInfoId);
        List<SpuSaleAttr> spuSaleAttrsList = spuSaleAttrMapper.selectList(wrapper);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrsList) {
            QueryWrapper<SpuSaleAttrValue> valueWrapper = new QueryWrapper();
            valueWrapper.eq("spu_id", spuInfoId);
            valueWrapper.eq("base_sale_attr_id", spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(valueWrapper);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }
        return spuSaleAttrsList;
    }

    @Override
    @GmallCache(prefix = "spuPoster")
    public List<SpuPoster> findSpuPosterBySpuId(Long spuId) {
        QueryWrapper<SpuPoster> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        return spuPosterMapper.selectList(wrapper);
    }


}
