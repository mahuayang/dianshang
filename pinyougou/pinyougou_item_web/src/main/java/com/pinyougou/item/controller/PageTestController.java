package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *模拟测试商品审核生成页面
 */
@RequestMapping("/test")
@RestController
public class PageTestController {
    @Value("${ITEM_HTML_PATH}")
    private  String ITEM_HTML_PATH;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;




    /**
     *  审核商品后生成商品 html 页面到指定路径
     * @param goodsIds  商品 id 集合
     * @return
     */
    @GetMapping("/audit")
    public String audit(Long[] goodsIds){
        if (goodsIds !=null && goodsIds.length>0){
            for (Long goodsId : goodsIds) {
                genItemHtml(goodsId);
            }
        }
        return "success";
    }

    private void genItemHtml(Long goodsId) {
        try {
            // 获取模板
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            // 获取模板需要的数据
            Map<String, Object> dataModel = new HashMap<>();
            // 根据商品 id 查询商品基本、描述、启用的 SKU 列表
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
            // 商品基本信息
            dataModel.put("goods", goods.getGoods());
            // 商品描述信息
            dataModel.put("goodsDesc", goods.getGoodsDesc());
            // 查询三级商品分类
            TbItemCat itemCat1 =
                    itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1", itemCat1.getName());
            TbItemCat itemCat2 =
                    itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2", itemCat2.getName());
            TbItemCat itemCat3 =
                    itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3", itemCat3.getName());
            // 查询 SKU 商品列表
            dataModel.put("itemList", goods.getItemList());
            // 输出到指定路径
            String fileName = ITEM_HTML_PATH+goodsId+".html";
            FileWriter fileWriter = new FileWriter(fileName);
            template.process(dataModel,fileWriter);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  删除商品后删除指定路径下的商品 html 页面
     * @param goodsIds  商品 id 集合
     * @return
     */
    @GetMapping("/delete")
    public String delete(Long[] goodsIds){
        if (goodsIds !=null && goodsIds.length>0){
            for (Long goodsId : goodsIds) {
                String fileName = ITEM_HTML_PATH+goodsId+".html";
                File file = new File(fileName);
                if (file.exists()){
                    file.delete();
                }
            }
        }
        return "success";
    }


}
