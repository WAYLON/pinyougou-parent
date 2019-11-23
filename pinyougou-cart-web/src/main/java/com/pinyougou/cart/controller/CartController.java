package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.group.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
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

        try {
            //1.从cookie中提取购物车
            List<Cart> cartList = findCartList();
            //2.调用服务方法操作购物车
            cartList  = cartService.addGoodsToCartList(cartList, itemId, num);
            //3.将新的购物车存入cookie
            String cartListString = JSON.toJSONString(cartList);
            CookieUtil.setCookie(request,response,"cartList",cartListString,3600*24,"UTF-8");
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //从cookie中提取购物车
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null || "".equals(cartListString)){
            cartListString="[]";
        }
        return JSON.parseArray(cartListString, Cart.class);
    }

}
