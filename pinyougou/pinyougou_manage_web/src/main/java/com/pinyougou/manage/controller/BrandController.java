package com.pinyougou.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 *
 */
@RequestMapping("/brand")
//@Controller
@RestController //组合了@ResponseBody、@Controller对类中的所有方法生效
public class BrandController {
    //注入代理对象
    @Reference
    private BrandService brandService;

    /**
     *
     * 分页查询品牌的第1页每页5条数据
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(Integer page,Integer rows){
       // return brandService.testPage(page,rows);
        return(List<TbBrand>) brandService.findPage(page,rows).getRows();
    }

    /**
     * 查询品牌列表
     * @return 品牌列表json格式字符串
     */
     /*@RequestMapping(value="/findAll", method = RequestMethod.GET)
    @ResponseBody*/
    @GetMapping("/findAll")
    public List<TbBrand> findAll(){
       // return brandService.queryAll();
        return brandService.findAll();
    }
    /**
     * 分页查询品牌
     * @param page 页号
     * @param rows 页大小
     * @return 分页对象
     */
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(defaultValue = "1")Integer page,
                               @RequestParam(defaultValue = "10")Integer rows){
        PageResult result = brandService.findPage(page, rows);
        return result;
    }
    /**
     * 保存品牌
     * @param brand 品牌
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return Result.ok("新增成功");
            //return new Result(true,"新增成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增失败");
        //return new Result(false,"新增失败");
    }
    /**
     * 根据id查询品牌
     * @param id 品牌id
     * @return 品牌
     */
    @GetMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }
    /**
     * 保存品牌
     * @param brand 品牌
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return Result.ok("修改成功");
             //new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
       // return new Result(false,"修改失败");
    }
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     *  根据条件分页查询
     * @param brand
     * @param page
     * @param rows
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                             @RequestParam(value = "page",defaultValue = "1")Integer page,
                             @RequestParam(value = "rows",defaultValue = "10")Integer rows){
        return brandService.search(brand,page,rows);
    }

    /**
     * 查询品牌列表，返回的数据格式符合 select2 格式
     * @return
     */
    @GetMapping("/selectOptionList")
    public List<Map<String,Object>> selectOptionList(){
        return brandService.selectOptionList();
    }
}
