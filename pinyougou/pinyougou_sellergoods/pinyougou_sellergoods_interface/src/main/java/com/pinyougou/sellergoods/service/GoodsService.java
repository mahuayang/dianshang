package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;


public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows,TbGoods goods);
    /**
     * 保存商品数据(基本,描述,sku列表)
     * @param goods
     * @return
     */
    void addGoods(Goods goods);

    Goods findGoodsById(Long id);

    void updateGoods(Goods goods);

    void updateStatus(Long[] ids, String status);

    void deleteGoodsByIds(Long[] ids);

    void marketableByIds(Long[] ids);
}
