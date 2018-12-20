package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemMapper itemMapper;


    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page,rows);
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(goods.get***())){
            criteria.andLike("***", "%" + goods.get***() + "%");
        }*/
        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());

    }
    /**
     * 保存商品数据(基本,描述,sku列表)
     * @param goods
     * @return
     */
    @Override
    public void addGoods(Goods goods) {
        //新增商品基本信息
        goodsMapper.insertSelective(goods.getGoods());
        //新增商品基本描述
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
        //保存商品sku列表
        saveItemList(goods);

    }

    private void saveItemList(Goods goods) {
    if ("1".equals(goods.getGoods().getIsEnableSpec())){
        // 如果启动规格，则需要按照规格生成不同的 SKU 商品
        for(TbItem item:goods.getItemList()){
            String title = goods.getGoods().getGoodsName();
            //组合规格选项形成sku标题
            Map<String,Object> map = JSON.parseObject(item.getSpec());
            Set<Map.Entry<String,Object>> entries = map.entrySet();
            for (Map.Entry<String,Object> entry : entries){
                title +=" " + entry.getValue().toString();
            }
            item.setTitle(title);

            setItemValue(goods, item);

            itemMapper.insertSelective(item);

        }
        }else {
        // 如果没有启动规格，则只存在一条 SKU 信息
        TbItem tbItem = new TbItem();
        //标题
        tbItem.setTitle(goods.getGoods().getGoodsName());
        //spec
        tbItem.setSpec("{}");

        //价格使用spu
        tbItem.setPrice(goods.getGoods().getPrice());
        //库存量9999
        tbItem.setNum(9999);
        //未启用. 0
        tbItem.setStatus("0");
        //是否默认,因为只有一个sku,所以肯定是默认
        tbItem.setIsDefault("1");

        setItemValue(goods,tbItem);
        itemMapper.insertSelective(tbItem);
        }
    }

    private void setItemValue(Goods goods, TbItem item) {
        // 图片
        List<Map> imgList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
        if (imgList != null && imgList.size()>0){
            //将商品的第一张图作为sku的图片
            item.setImage(imgList.get(0).get("url").toString());
        }
        //商品分类id
        item.setCategoryid(goods.getGoods().getCategory3Id());
        // 商品分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //创建时间
        item.setCreateTime(new Date());
        //更新时间
        item.setUpdateTime(item.getCreateTime());
        //spu商品id
        item.setGoodsId(goods.getGoods().getId());
        // 商家 id
        item.setSellerId(goods.getGoods().getSellerId());
        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
    }
}
