package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.model.product.BaseSaleAttr;
import com.arzhu.sph_gmall.product.mapper.BaseSaleAttrMapper;
import com.arzhu.sph_gmall.product.service.BaseSaleAttrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: BaseSaleAttrListServiceImpl
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 15:28
 * @Version 1.0
 */
@Service
public class BaseSaleAttrServiceImpl extends ServiceImpl<BaseSaleAttrMapper, BaseSaleAttr> implements BaseSaleAttrService {
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Override
    public List<BaseSaleAttr> getAllList() {
        return baseSaleAttrMapper.selectList(null);
    }
}
