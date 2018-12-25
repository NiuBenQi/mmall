package com.casic.web.controller.guanxian;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.casic.service.guanxian.tcuserService;





@Controller
@RequestMapping(value="/tttest")
public class testController {
	@Autowired
	private tcuserService tcuserservice;
	@RequestMapping(value="/ceshi")
	public String getpage(){
		String usercode="0011301231001";
		tcuserservice.selectByUsercode(usercode);
		return "tiles.guanxian.body";
	}
}
