package com.arzhu.sph_gmall.product.service;

import com.arzhu.sph_gmall.model.product.BaseAttrInfo;
import com.arzhu.sph_gmall.model.product.BaseAttrValue;
import com.arzhu.sph_gmall.model.product.BaseSaleAttr;

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
public interface ManagerService {
    List getCategory1();

    List getCategory2(Integer category1Id);

    List getCategory3(Integer category2Id);

    List<BaseAttrInfo> getAttrInfoList(Integer category1Id, Integer category2Id, Integer category3Id);

    List<BaseAttrValue> getAttrValueList(Integer attrId);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 查找所有的 baseSaleAttr
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();
}
