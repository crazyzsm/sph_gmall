package com.arzhu.sph_gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.arzhu.sph_gmall.item.service.ItemService;
import com.arzhu.sph_gmall.model.product.*;
import com.arzhu.sph_gmall.product.client.ProductFeignClient;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: ItemServiceImpl
 * Package: com.arzhu.sph_gmall.item.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/16 16:02
 * @Version 1.0
 */
@Service

public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        HashMap<String, Object> resMap = new HashMap<>();
//        先获取 sku信息和图片列表
        SkuInfo skuInfo = productFeignClient.getAttrValueList(skuId);  // sku信息
        resMap.put("skuInfo",skuInfo);
//        获取实时价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        resMap.put("price",skuPrice);
//        判断
        if(skuInfo!=null){
            Long spuId = skuInfo.getSpuId();
//            获取三级分类
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            resMap.put("categoryView",categoryView);
//            获取销售数据和选中状态
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, spuId);
            resMap.put("spuSaleAttrList",spuSaleAttrListCheckBySku);
//            获取商品切换数据
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(spuId);
            resMap.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));
//            获取海报信息
            List<SpuPoster> spuPosterBySpuId = productFeignClient.findSpuPosterBySpuId(spuId);
            resMap.put("spuPosterList",spuPosterBySpuId);
        }
//        获取平台信息
        List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
//        处理数据
        List<Map<String, String>> spuAttrList = attrList.stream().map(baseAttrInfo -> {
            Map<String, String> map = new HashMap<>();
            map.put("attrName", baseAttrInfo.getAttrName());
            map.put("attrValue", baseAttrInfo.getAttrValueList().get(0).getValueName());
            return map;
        }).collect(Collectors.toList());
        resMap.put("skuAttrList", spuAttrList);
        return resMap;
    }
}
