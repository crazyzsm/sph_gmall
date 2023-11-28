package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.model.product.BaseTrademark;
import com.arzhu.sph_gmall.product.mapper.BaseTrademarkMapper;
import com.arzhu.sph_gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName: BaseTrademarkServiceImpl
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/14 21:09
 * @Version 1.0
 */
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark> implements BaseTrademarkService {
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public IPage<BaseTrademark> getPage(Page<BaseTrademark> pageParam) {
        QueryWrapper<BaseTrademark> queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("id");  // 降序 排序
        Page<BaseTrademark> page = baseTrademarkMapper.selectPage(pageParam, queryWrapper);
        return page;

    }
}
