package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.model.product.BaseAttrInfo;
import com.arzhu.sph_gmall.model.product.BaseAttrValue;
import com.arzhu.sph_gmall.model.product.BaseCategory3;
import com.arzhu.sph_gmall.model.product.BaseSaleAttr;
import com.arzhu.sph_gmall.product.mapper.*;
import com.arzhu.sph_gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: ManagerServiceImpl
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/13 21:04
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)  // 添加事务
public class ManagerServiceImpl implements ManagerService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;



    @Override
    public List getCategory1() {
        System.out.println("进入 service。。。。");
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List getCategory2(Integer category1Id) {
        return baseCategory2Mapper.selectByCategory1Id(category1Id);
    }

    @Override
    public List getCategory3(Integer category2Id) {
        QueryWrapper<BaseCategory3> baseCategory3QueryWrapper = new QueryWrapper<>();
        baseCategory3QueryWrapper.eq("category2_id" ,category2Id );
        return baseCategory3Mapper.selectList(baseCategory3QueryWrapper);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(Integer category1Id, Integer category2Id, Integer category3Id) {
        return baseAttrInfoMapper.selectAttrInfoList(category1Id,category2Id,category3Id);
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Integer attrId) {
        QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper = new QueryWrapper<>();
        baseAttrValueQueryWrapper.eq("attr_id", attrId);
        return baseAttrValueMapper.selectList(baseAttrValueQueryWrapper);
    }

    /**
     * 这里将遇到两张表的添加
     * 1、base_attr_info
     * 2、base_attr_value
     * 所以这里需要事务
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Long baseAttrInfoId = baseAttrInfo.getId();
        if(baseAttrInfoId!=null){
//            此时 应该走 修改
            baseAttrInfoMapper.updateById(baseAttrInfo);
        }else{
//            此时应该走新增
            baseAttrInfoMapper.insert(baseAttrInfo);
        }
        baseAttrInfoId = baseAttrInfo.getId();  // 在赋值一次 获取 数据库的自增ID
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        //        要是有属性的话 先删除 然后再添加
//            删除属性
        QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper = new QueryWrapper<>();
        baseAttrValueQueryWrapper.eq("attr_id",baseAttrInfoId);
        baseAttrValueMapper.delete(baseAttrValueQueryWrapper);

        int size = attrValueList.size();
        if(attrValueList!=null && size!=0){
//            代表有 属性名称 也需要添加
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(baseAttrInfoId);
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }else{
            System.out.println("暂时没属性。。。。");
        }


    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }
}
