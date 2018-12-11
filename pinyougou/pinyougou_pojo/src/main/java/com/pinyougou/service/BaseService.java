package com.pinyougou.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.vo.PageResult;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T>  {
    /**
     * 根据主键查询
     * @param id
     * @return
     */
    public T findOne(Serializable id);

    /**
     * 查询全部
     * @return
     */
    public List<T> findAll();

    /**
     * 根据条件查询列表
     * @param t
     * @return
     */
    public List<T> findByWhere(T t);

    /**
     * 分页查询列表
     * @param page
     * @param rows
     * @return
     */
    public PageResult findPage(Integer page, Integer rows);

    /**
     *  根据条件分页查询列表
     * @param page
     * @param rows
     * @param t
     * @return
     */
    public PageResult findPage(Integer page,Integer rows,T t);

    /**
     *  新增
     * @param t
     */
    public void add(T t);

    /**
     * 根据主键更新
     * @param t
     */
    public void update(T t);

    /**
     *   批量删除
     * @param ids
     */
    public void deleteByIds(Serializable[] ids);

}
