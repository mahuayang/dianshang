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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 *
 */
@Transactional
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
        //不查询删除商品
        criteria.andNotEqualTo("isDelete","1");


        //商家规定
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId",goods.getSellerId());
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus",goods.getAuditStatus());
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName","%"+goods.getGoodsName()+"%");
        }
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



    @Override
    public Goods findGoodsById(Long id) {
        return findGoodsByIdAndStatus(id,null);

    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String itemStatus) {

        Goods goods = new Goods();
        //查询商品spu
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        goods.setGoods(tbGoods);

        //查询商品描述
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        goods.setGoodsDesc(tbGoodsDesc);
        //查询商品SKU列表
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId",goodsId);
        if (!StringUtils.isEmpty(itemStatus)){
            criteria.andEqualTo("status",itemStatus);
        }

        example.orderBy("isDefault").desc();
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }

    @Override
    public void updateGoods(Goods goods) {
        //更新商品基本信息
        goods.getGoods().setAuditStatus("0");//修改过则重新设置为未审核
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
        //更新商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
        //删除原有的sku列表
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);
        //保存商品SKU列表
        saveItemList(goods);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        //批量更新商品的审核状态
        goodsMapper.updateByExampleSelective(goods,example);
        //如果是审核通过则将sku设置为启用状态
        if ("2".equals(status)){
            //更新的内容
            TbItem item =new TbItem();
            item.setStatus("1");
            //更新条件
            Example itemExample = new Example(TbItem.class);
            itemExample.createCriteria().andIn("goodsId", Arrays.asList(ids));
            itemMapper.updateByExampleSelective(item,itemExample);
        }
    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
        TbGoods goods = new TbGoods();
        goods.setIsDelete("1");
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        //批量更新商品的删除状态为删除
        goodsMapper.updateByExampleSelective(goods,example);
    }

    @Override
    public void marketableByIds(Long[] ids) {
        TbGoods goods = new TbGoods();
        goods.setIsMarketable("1");
        goods.setIsMarketable("0");
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        //批量更新商品的状态为上下架状态
        goodsMapper.updateByExampleSelective(goods,example);
    }

    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status) {
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("status",status).andIn("goodsId",Arrays.asList(ids));

        return itemMapper.selectByExample(example);
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
