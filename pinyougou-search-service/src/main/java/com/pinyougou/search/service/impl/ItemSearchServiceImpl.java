package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));//去点空格
        //1.查询列表
        map.putAll(searchList(searchMap));
        //2.分组查询分类列表
        List<String> categoryList = categoryList(searchMap);
        map.put("categoryList",categoryList);
        //3.查询品牌和规格列表
        String categoryName=(String)searchMap.get("category");
        if(!"".equals(categoryName)){//如果有分类名称
            map.putAll(searchBrandAndSpecList(categoryName));
        }else{//如果没有分类名称，按照第一个查询
            if(categoryList.size()>0){
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }
        return map;
    }

    /**
     * 根据关键字搜索列表
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map map = new HashMap<>();
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //设置高亮选项
        query.setHighlightOptions(highlightOptions);
        //1.1按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.2按照商品分类查询
        if(!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按照品牌查询
        if(!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4按照规格查询
        if(!"".equals(searchMap.get("spec"))) {
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for(String key :specMap.keySet()){
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.5价格过滤
        if(!"".equals(searchMap.get("price"))) {
            String[] price = ((String) searchMap.get("price")).split("-");
            if(!price[0].equals("0")){
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);// >=
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);// <=
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.6分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageNo==null){
            pageNo=1;
        }
        if(pageNo==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);//从第几条记录查询
        query.setRows(pageSize);
        //1.7排序
        //升序降序
        String sortValue= (String) searchMap.get("sort");
        //排序字段
        String sortFiled = (String) searchMap.get("sortField");
        if(sortFiled != null && !sortFiled.equals("")) {
            if (sortValue.equals("ASC")) {
                Sort orders = new Sort(Sort.Direction.ASC, "item_"+sortFiled);
                query.addSort(orders);
            }
            if (sortValue.equals("DESC")) {
                Sort orders = new Sort(Sort.Direction.DESC, "item_"+sortFiled);
                query.addSort(orders);
            }
        }

        /*获取高亮结果集*/
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //循环高亮入口集合
        for (HighlightEntry<TbItem> h : page.getHighlighted()) {
            //获取原实体类
            TbItem item = h.getEntity();
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                //设置高亮的结果
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());//返回总页数
        map.put("total", page.getTotalElements());//返回总记录数

        return map;
    }

    /**
     * 分组查询，查询商品分类列表
     *
     * @return
     */
    private List<String> categoryList(Map searchMap) {
        List<String> strings = new ArrayList<>();
        Query query = new SimpleQuery("*:*");
        //根据关键字查询 where
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //group by
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果对象
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : entryList) {
            strings.add(entry.getGroupValue());
        }
        return strings;
    }

    /**
     * 查询品牌和规格列表
     * @param category 商品分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        //1.根据商品名称获取模板ID
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (templateId != null) {
            //2.根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList", brandList);
            //3.根据模板ID查询品牌列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList", specList);
        }
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
