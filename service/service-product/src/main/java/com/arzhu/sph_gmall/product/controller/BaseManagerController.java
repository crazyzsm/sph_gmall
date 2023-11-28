package com.arzhu.sph_gmall.product.controller;

import com.arzhu.sph_gmall.common.result.Result;
import com.arzhu.sph_gmall.model.product.BaseAttrInfo;
import com.arzhu.sph_gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

/**
 * ClassName: BaseManagerController
 * Package: com.arzhu.sph_gmall.product.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/13 21:02
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
//@CrossOrigin(origins = "*",  allowedHeaders = "*")
public class BaseManagerController {
    @Autowired
    private ManagerService managerService;


    /**
     * 查找分类1
     * @return
     */
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        return Result.ok(managerService.getCategory1());
    }
    @GetMapping("/getCategory2/{category1Id}")
    public  Result getCategory2(@PathVariable("category1Id") Integer category1Id){

        return Result.ok(managerService.getCategory2(category1Id));
    }
    @GetMapping("/getCategory3/{category2Id}")
    public  Result getCategory3(@PathVariable("category2Id") Integer category2Id){
        return Result.ok(managerService.getCategory3(category2Id));
    }
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public  Result getAttrInfoList(@PathVariable("category1Id") Integer category1Id,@PathVariable("category2Id") Integer category2Id,@PathVariable("category3Id") Integer category3Id){
        return Result.ok(managerService.getAttrInfoList(category1Id,category2Id,category3Id));
    }
    @GetMapping("/getAttrValueList/{attrId}")
    public  Result getAttrValueList(@PathVariable("attrId") Integer attrId){
        return Result.ok(managerService.getAttrValueList(attrId));
    }

    /**
     * 新增和修改接口 通过 id 判断是要新增还是要修改
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        managerService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }



}
