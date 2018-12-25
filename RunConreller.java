package com.casic.web.controller.guanxian;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//运营模块
@Controller
@RequestMapping("/run")
public class RunConreller {
	//运行信息填报
	@RequestMapping("/getbody")
	public String runinput(){
		return "run.info";
	}
	@RequestMapping("/runshenpi")
	public String runshenpi(){
		return "run.shenpi";
	}
	@RequestMapping("/runupdate")
	public String runupdate(){
		return "run.update";
	}
	@RequestMapping("/runselect")
	public String runselect(){
		return "run.select";
	}
	@RequestMapping("/add")
	public String add(){
		return "run.add";
	}

}
