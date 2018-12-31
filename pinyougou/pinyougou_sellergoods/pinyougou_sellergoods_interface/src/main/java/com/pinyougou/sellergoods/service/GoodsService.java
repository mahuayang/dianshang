package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

import java.util.List;


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

    /**
     * 根据商品SPU id 集合和状态查询这些商品对应的sku列表商品
     * @param ids 商品SPU id 集合
     * @param status sku商品状态
     * @return ku商品列表
     */
    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);

    /**
     *根据商品id查询商品基本,描述,SKU 列表
     * @param goodsId 商品id
     * @param itemStatus 是否开启
     * @return 商品
     */
    Goods findGoodsByIdAndStatus(Long goodsId, String itemStatus);

}
