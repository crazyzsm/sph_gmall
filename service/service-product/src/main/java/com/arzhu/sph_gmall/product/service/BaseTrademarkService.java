package com.arzhu.sph_gmall.product.service;

import com.arzhu.sph_gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * ClassName: BaseTrademarkService
 * Package: com.arzhu.sph_gmall.product.service
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/14 21:09
 * @Version 1.0
 */
public interface BaseTrademarkService extends IService<BaseTrademark> {
    /**
     * 分页查询品牌数据
     * @param pageParam
     * @return
     */
    IPage<BaseTrademark> getPage(Page<BaseTrademark> pageParam);
}
