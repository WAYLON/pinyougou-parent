package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

/**
 * 品牌接口
 * @author Administrator
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();

	/**
	 * 品牌分页
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(int pageNum , int pageSize);

	/**
	 * 增加品牌
	 * @param tbBrand
	 */
	public void add(TbBrand tbBrand);
}
