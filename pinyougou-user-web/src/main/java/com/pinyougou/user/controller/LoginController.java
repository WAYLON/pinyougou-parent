package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangl
 */
@RestController
@RequestMapping("/login")
public class LoginController {	
	@RequestMapping("/name")
	public Map showName(){
		//得到登陆人账号
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map=new HashMap<>();
		map.put("loginName", name);
		return map;		
	}	
}
