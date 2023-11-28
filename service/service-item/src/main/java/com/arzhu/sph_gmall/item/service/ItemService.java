package com.arzhu.sph_gmall.item.service;

import java.util.Map;

/**
 * ClassName: ItemService
 * Package: com.arzhu.sph_gmall.item.service
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/16 16:01
 * @Version 1.0
 */

public interface ItemService {
    /**
     * 根据
     * @param skuId
     * @return
     */
    Map<String,Object> getBySkuId(Long skuId);
}
