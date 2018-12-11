package com.pinyougou.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RequestMapping("/brand")
@RestController
public class BrandController {
    @Reference
    private BrandService brandService;

    @GetMapping("/testPage")
    public List<TbBrand> testPage(Integer page,Integer rows){
       // return brandService.testPage(page,rows);
        return(List<TbBrand>) brandService.findPage(page,rows).getRows();
    }


    @GetMapping("/findAll")
    public List<TbBrand> findAll(){
       // return brandService.queryAll();
        return brandService.findAll();
    }
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(defaultValue = "1")Integer page,
                               @RequestParam(defaultValue = "10")Integer rows){
        PageResult result = brandService.findPage(page, rows);
        return result;
    }
}
