package com.casic.web.controller.guanxian;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//已建设模块
@Controller
@RequestMapping("/AlreadBuild")
public class GxAlreadBuildController {
	//信息填报
	@RequestMapping("/alerdyInfo")
	public String alerdyInfo(){
		return "tiles.alreadBuild";
	}
	//新增信息填报
	@RequestMapping("/input")
	public String safety(){
		return "tiles.fillReport";
	}
	//已建设信息审批
	@RequestMapping("approval")
	public String approval(){
		return "tiles.approval";
	}
	//已建设信息修改
	@RequestMapping("edit")
	public String edit(){
		return "tiles.edit";
	}
	//已建设信息查询
	@RequestMapping("select")
	public String select(){
		return "tiles.select";
	}
}
