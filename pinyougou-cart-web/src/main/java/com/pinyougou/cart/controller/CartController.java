package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.group.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wangl
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        try {
            List<Cart> cartList = findCartList();
            //1.从cookie中提取购物车
            //2.调用服务方法操作购物车
            cartList  = cartService.addGoodsToCartList(cartList, itemId, num);
            //未登录
            if(name.equals("anonymousUser")){
                //3.将新的购物车存入cookie
                String cartListString = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",cartListString,3600*24,"UTF-8");
                System.out.println("向cookie中存入购物车");
            }else {
                //已登录
                cartService.saveCartListToRedis(name,cartList);
            }
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString  = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null || "".equals(cartListString)){
            cartListString="[]";
        }
        List<Cart> cartListCookie = JSON.parseArray(cartListString, Cart.class);
        //如果未登录
        if("anonymousUser".equals(username)){
            return cartListCookie;
        }else{
            //从redis中提取
            List<Cart> cartList_redis =cartService.findCartListFromRedis(username);
            //如果本地存在购物车
            if(cartListCookie.size()>0){
                //合并购物车
                cartList_redis=cartService.mergeCartList(cartList_redis, cartListCookie);
                //清除本地cookie的数据
                util.CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username, cartList_redis);
                System.out.println("执行合并购物车的逻辑");
            }
            return cartList_redis;
        }
    }
}
