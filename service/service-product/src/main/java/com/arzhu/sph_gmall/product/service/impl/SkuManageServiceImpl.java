package com.arzhu.sph_gmall.product.service.impl;

import com.arzhu.sph_gmall.common.cache.GmallCache;
import com.arzhu.sph_gmall.common.cache.GmallCacheOld;
import com.arzhu.sph_gmall.common.constant.RedisConst;
import com.arzhu.sph_gmall.model.product.*;
import com.arzhu.sph_gmall.product.mapper.*;
import com.arzhu.sph_gmall.product.service.SkuManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.CollectionUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: SkuManageServiceImpl
 * Package: com.arzhu.sph_gmall.product.service.impl
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 21:01
 * @Version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SkuManageServiceImpl extends ServiceImpl<SkuManageMapper, SkuInfo> implements SkuManageService {
    @Autowired
    private SkuManageMapper skuManageMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

//    @Autowired
    /**
     * 1、创建 sku_info 表
     * 2、创建 sku_image 表
     * 3、创建 sku_sale_attr_value 表
     * 4、创建 sku_attr_value 表
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
//        1、创建 SKU_info 表
        skuManageMapper.insert(skuInfo);
        Long SPU_ID = skuInfo.getSpuId();
        Long SKU_ID = skuInfo.getId();
//        2、创建 sku_image 表
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(!CollectionUtils.isEmpty(skuImageList)){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(SKU_ID);
                skuImageMapper.insert(skuImage);
            }
        }
//        3、创建 sku_sale_attr_value 表
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(!CollectionUtils.isEmpty(skuSaleAttrValueList)){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(SKU_ID);
                skuSaleAttrValue.setSpuId(SPU_ID);
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
//        4、创建 sku_attr_value 表
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(SKU_ID);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }

    }

    @Override
    public IPage<SkuInfo> findPageSkuList(Page<SkuInfo> page) {
        Page<SkuInfo> skuInfoPage = skuManageMapper.selectPage(page,null);
        return skuInfoPage;
    }

    @Override
    public void onSale(Integer skuId) {
//        SkuInfo skuInfo = new SkuInfo(skuId.longValue());
//        skuInfo.setIsSale(1);
//        UpdateWrapper<SkuInfo> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("id", skuId);
//        skuManageMapper.update(skuInfo,updateWrapper);
        SkuInfo skuInfo = new SkuInfo(skuId.longValue());
        skuInfo.setIsSale(1);
        skuManageMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(Integer skuId) {
//        SkuInfo skuInfo = new SkuInfo(skuId.longValue());
//        skuInfo.setIsSale(0);
//        UpdateWrapper<SkuInfo> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("id", skuId);
//        skuManageMapper.update(skuInfo,updateWrapper);
        SkuInfo skuInfo = new SkuInfo(skuId.longValue());
        skuInfo.setIsSale(0);
        skuManageMapper.updateById(skuInfo);
    }

    /**
     * 从 从 redis 或者 数据库中拿到 skuInfo
     * 主要是 要考虑 缓存穿透 的情况
     * @param skuId
     * @return
     */
    @Override
    @GmallCache(prefix = "sku")
    public SkuInfo getSkuInfo(Long skuId) {
        //     return getSkuInfoRedis(skuId);
//        return getSkuInfoRedisson(skuId);
      return getSkuInfoDB(skuId);
    }


    public SkuInfo getSkuInfoDB(Long skuId){
        SkuInfo skuInfo = skuManageMapper.selectById(skuId);
        QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        List<SkuImage> skuImageList = skuImageMapper.selectList(wrapper);
        if(skuInfo!=null){
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }

    /**
     * 使用 redisson 获取分布式锁
     * @return
     */
    private SkuInfo getSkuInfoRedisson(Long skuId) {
        //        1、定义 key key 为 sku:id:skuInfo
        String infoKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
//        2、尝试从缓存中获取
        SkuInfo skuInfo = (SkuInfo)redisTemplate.opsForValue().get(infoKey);
        if(skuInfo==null){
//            不存在 那么就需要往 redis 中写入 那么就要使用分布式锁的方案
            String lockKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
            RLock lock = redissonClient.getLock(lockKey);
//            尝试获取锁 获取锁的最大等待时间为 1s 锁的过期时间为 100s
            try {
                boolean res =  lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);

                if(res){
//                获取到了锁的情况 开始执行业务逻辑
                    skuInfo = skuManageMapper.selectById(skuId);
//                如果 获取到的 是 null 那么就直接设置为 null 然后 设置过期时间
                    if(skuInfo == null){
                        redisTemplate.opsForValue().set(infoKey, new SkuInfo(), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                        return skuInfo;
                    }
                    QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
                    wrapper.eq("sku_id",skuId);
                    List<SkuImage> skuImageList = skuImageMapper.selectList(wrapper);
                    skuInfo.setSkuImageList(skuImageList);
                    redisTemplate.opsForValue().set(infoKey, skuInfo);

                }else{
                        Thread.sleep(1000);
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                //                开始解锁
                lock.unlock();
            }

        }else{

        }
        return skuInfo;
    }

    private SkuInfo getSkuInfoRedis(Long skuId){
//        1、定义 key key 为 sku:id:skuInfo
        String infoKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
//        2、尝试从缓存中获取
        SkuInfo skuInfo = (SkuInfo)redisTemplate.opsForValue().get(infoKey);
        if(skuInfo==null){
//            不存在 那么就需要往 redis 中写入 那么就要使用分布式锁的方案
            String uuid = UUID.randomUUID().toString();
//            获取锁 设置 key = lock value = uuid 3秒过期 这里暂时不考虑过期续约
            String lockKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
            Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
            if(lock){
//                获取到了锁的情况 开始执行业务逻辑
                skuInfo = skuManageMapper.selectById(skuId);
//                如果 获取到的 是 null 那么就直接设置为 null 然后 设置过期时间
                if(skuInfo == null){
                    redisTemplate.opsForValue().set(infoKey, new SkuInfo(), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                    //                开始解锁
                    redisUnLock(lockKey,uuid);
                    return skuInfo;
                }
                QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
                wrapper.eq("sku_id",skuId);
                List<SkuImage> skuImageList = skuImageMapper.selectList(wrapper);
                skuInfo.setSkuImageList(skuImageList);
                redisTemplate.opsForValue().set(infoKey, skuInfo);
//                开始解锁
                redisUnLock(lockKey,uuid);
            }else{
//                没获取到锁，那么就自旋 然后等待
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    getSkuInfoRedis(skuId);
                }
            }
        }else{

        }
        return skuInfo;
    }

    /**
     * redis 的解锁方法
     */
    private void redisUnLock(String key,String uuid){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
// 设置lua脚本返回的数据类型
       DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
// 设置lua脚本返回类型为Long
       redisScript.setResultType(Long.class);
       redisScript.setScriptText(script);
        Object res = redisTemplate.execute(redisScript, Arrays.asList(key), uuid);
    }

    @Override
    @GmallCache(prefix = "categoryView")
    public BaseCategoryView getCategoryView(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return skuManageMapper.selectById(skuId).getPrice();
    }

    @Override
    @GmallCache(prefix = "skuValue")
    public Map getSkuValueIdsMap(Long spuId) {
        Map<Object, Object> map = new HashMap<>();
        // key = 125|123 ,value = 37
        List<Map> mapList = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        if (mapList != null && mapList.size() > 0) {
            // 循环遍历
            for (Map skuMap : mapList) {
                // key = 125|123 ,value = 37
                map.put(skuMap.get("value_ids"), skuMap.get("sku_id"));
            }
        }
        return map;
    }
}
