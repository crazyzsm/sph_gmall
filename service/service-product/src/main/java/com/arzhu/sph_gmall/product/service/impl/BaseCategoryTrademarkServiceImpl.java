package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.model.product.BaseCategoryTrademark;
import com.arzhu.sph_gmall.model.product.BaseTrademark;
import com.arzhu.sph_gmall.model.product.CategoryTrademarkVo;
import com.arzhu.sph_gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.arzhu.sph_gmall.product.mapper.BaseTrademarkMapper;
import com.arzhu.sph_gmall.product.service.BaseCategoryTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: BaseCategoryTrademarkService
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/14 21:56
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseCategoryTrademarkServiceImpl extends ServiceImpl<BaseCategoryTrademarkMapper, BaseCategoryTrademark> implements BaseCategoryTrademarkService {
    @Autowired
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Override
    public List<BaseTrademark> findTrademarkByCategoryList(Integer category3) {
        QueryWrapper<BaseCategoryTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3);
        List<BaseCategoryTrademark> baseCategoryTrademarks = baseCategoryTrademarkMapper.selectList(wrapper);
//       CollectionUtils
        if(CollectionUtils.isEmpty(baseCategoryTrademarks)){
//            为空的话 那么 不处理咯
        }else{
//            不为空的话 那么就要根据这些list 去 base_trademarks 查找了

//            使用流 去做 把 baseCategoryTrademarks 中的 id 生成一个 idList
            List<Long> trademarkIdList = baseCategoryTrademarks.stream().map(baseCategoryTrademark ->
                    baseCategoryTrademark.getTrademarkId()).collect(Collectors.toList());
            return baseTrademarkMapper.selectBatchIds(trademarkIdList);
        }
        return null;
    }

    @Override
    public List<BaseTrademark> findCurrentTrademarkList(Integer category3Id) {
//        1、先查找出当前已经关联的品牌
        QueryWrapper<BaseCategoryTrademark> bctWrapper = new QueryWrapper<>();
        bctWrapper.eq("category3_id", category3Id);
        List<BaseCategoryTrademark> baseCategoryTrademarks = baseCategoryTrademarkMapper.selectList(bctWrapper);
//        2、得到这些数据的 trademarkId
        List<Long> trademarkIds = baseCategoryTrademarks.stream().map(baseCategoryTrademark -> baseCategoryTrademark.getTrademarkId()).collect(Collectors.toList());
//        3、过滤掉这些已经被关联的数据
        List<BaseTrademark> trademarkList = baseTrademarkMapper.selectList(null).stream().filter(baseTrademark -> !trademarkIds.contains(baseTrademark.getId())
        ).collect(Collectors.toList());
        return trademarkList;
    }

    @Override
    public void saveCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo) {
        //        categoryTrademarkVo
        for (Long trademarkId : categoryTrademarkVo.getTrademarkIdList()) {
            BaseCategoryTrademark baseCategoryTrademark = new BaseCategoryTrademark();
            baseCategoryTrademark.setTrademarkId(trademarkId);
            baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
            this.save(baseCategoryTrademark);
        }
    }

    @Override
    public void removeCategoryTrademark(Integer category3Id, Integer trademarkId) {
        QueryWrapper<BaseCategoryTrademark> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", category3Id);
        wrapper.eq("trademark_id", trademarkId);
        this.remove(wrapper);
    }
}
