package com.arzhu.sph_gmall.product.service;

import com.arzhu.sph_gmall.model.product.BaseSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName: BaseSaleAttrListService
 * Package: com.arzhu.sph_gmall.product.service
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 15:28
 * @Version 1.0
 */
public interface BaseSaleAttrService extends IService<BaseSaleAttr> {
    /**
     * 获取所有的数据
     * @return
     */
    List<BaseSaleAttr> getAllList();
}
