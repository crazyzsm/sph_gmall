package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.model.product.BaseAttrInfo;
import com.arzhu.sph_gmall.product.mapper.BaseAttrInfoMapper;
import com.arzhu.sph_gmall.product.service.BaseAttrInoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: BaseAttrInoServiceImpl
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/16 22:15
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseAttrInoServiceImpl implements BaseAttrInoService {
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Override
    public List<BaseAttrInfo> getAttrListBySkuId(Long skuId) {
        return baseAttrInfoMapper.getAttrListBySkuId(skuId);
    }
}
