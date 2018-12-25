package com.casic.web.controller.guanxian;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.casic.model.Page;
import com.casic.model.Tdadmindiv;
import com.casic.model.Tdplanstage;
import com.casic.model.Tdprovince;
import com.casic.model.guanxian.gxbuild;
import com.casic.model.guanxian.gxbuildapply;
import com.casic.service.ApplyProjectService;
import com.casic.service.DrProjectInfoService;
import com.casic.service.PlanService;
import com.casic.service.TcUserService;
import com.casic.service.TdAdminDivService;
import com.casic.service.TdPipeUsageService;
import com.casic.service.TdprovinceService;
import com.casic.service.YearprojectService;
import com.casic.service.guanxian.GxbuildService;
import com.casic.vo.PageModel;

@Controller
@RequestMapping("/gxbuild")
public class GxbuildController {
	@Autowired
	//项目基本信息
	private GxbuildService gxbuildservice;
	@Autowired
	private PlanService planService;
	// 管线种类(多对多)
	@Autowired
	private TcUserService tcUserService;
	// 省市县表
	@Autowired
	private TdAdminDivService divService;
	// 管线管线用途类别
	@Autowired
	private TdPipeUsageService tdPipeUsageService;
	// 省级用户
	@Autowired
	private TdprovinceService tdprovinceService;
	@Autowired
	private ApplyProjectService applyProjectService;
	@Autowired
	private DrProjectInfoService drProjectInfoService;
	@Autowired
	private YearprojectService yearprojectService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}
	/**
	 * 建设1.1填报
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public String view(Model model, HttpServletRequest request, HttpSession session) {
		// 用户id
		String proviceId = "";
		String cityId = "";
		String countryId = "";
		// 用户类型
		String userLevelId = (String) session.getAttribute("userLevelId");
		if (userLevelId != null && !userLevelId.equals("")) {
			// 市级用户
			if (userLevelId.equals("3")) {
				proviceId = (String) session.getAttribute("provinceId");
				cityId = (String) session.getAttribute("cityId");
				// 县级
			} else if (userLevelId.equals("4")) {
				countryId = (String) session.getAttribute("countyId");
				proviceId = (String) session.getAttribute("provinceId");
				cityId = (String) session.getAttribute("cityId");
			}
		}
		// 省
		Tdprovince tdadmindiv = tdprovinceService.selectByPrimaryKey(proviceId);
		// 市
		Tdadmindiv tdadmindiv1 = divService.selectByPrimaryKey(cityId);
		// 县
		Tdadmindiv tdadmindiv2 = divService.selectByPrimaryKey(countryId);
		
		model.addAttribute("tdadmindiv", tdadmindiv);
		model.addAttribute("tdadmindiv1", tdadmindiv1);
		model.addAttribute("tdadmindiv2", tdadmindiv2);
		return "tiles.gxbuild.body";
	}

	/* 查询数据展示 */
	/*@ResponseBody
	@RequestMapping(value = "/getList", method = { RequestMethod.GET, RequestMethod.POST })
	public PageModel appList(Page page, HttpServletRequest request, TbprojectinfoVo vo, HttpSession session) {
		String userLevelId = (String) session.getAttribute("userLevelId");
		// 传入用户级别
		String countryId = (String) session.getAttribute("countyId");
		String proviceId = (String) session.getAttribute("provinceId");
		String cityId = (String) session.getAttribute("cityId");
		if (proviceId != null && !proviceId.equals("")) {
			vo.setProvinceid(session.getAttribute("provinceId").toString());
		}
		if (cityId != null && !cityId.equals("")) {
			vo.setCityid(session.getAttribute("cityId").toString());
		}
		if (countryId != null && !countryId.equals("")) {
			vo.setCountyid(session.getAttribute("countyId").toString());
		}
		vo.setUserlevelid(userLevelId);
		PageModel pm = applyProjectService.findByPage(page, vo);

		return pm;
	}*/

	/**
	 * 调用添加页面填写数据
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/input", method = { RequestMethod.GET, RequestMethod.POST })
	public String viewProvince(HttpSession session, Model model) {
		List<Tdplanstage> planStageList = planService.planStageList();
		model.addAttribute("planStageList", planStageList);
		return "tiles.gxbuild.bodyAdd";
	}
	
	/**
	 * 添加基本信息
	 * @param request
	 * @param session
	 * @param gxbuildapply
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/saveForm", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String,Object> saveForm(HttpServletRequest request,HttpSession session, gxbuildapply gxbuildapply)throws Exception{
		//创建一个map用于返回
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//获取session数据
		String usercode = (String)session.getAttribute("userCode");//用于 创建人
		//省市县
		String provinceid = (String)session.getAttribute("provinceId");//省
		String cityid = (String)session.getAttribute("cityId");//市
		String countyid = (String)session.getAttribute("countyId");//县
		if(usercode != null){
			gxbuildapply.setCountyid(countyid);//县
			gxbuildapply.setCityid(cityid);
			gxbuildapply.setProvinceid(provinceid);
			gxbuildapply.setIssubmit("0");
			Map<String,Object> res = new HashMap<String,Object>();
			res=gxbuildservice.saveForm(gxbuildapply,usercode);
			modelMap.put("result", res.get("state"));
		}else{
			modelMap.put("result", "failure");
		}
		return modelMap;
	}
	
	/**
	 * 查询
	 * @param page
	 * @param request
	 * @param gxbuildpy
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public PageModel list(Page page,HttpServletRequest request,gxbuildapply gxbuildpy, HttpSession session ){
		PageModel pm = gxbuildservice.findByPage(page,gxbuildpy);
		return pm;
	}
	/**
	 * 模糊查询
	 * @param page
	 * @param request
	 * @param gxbuildpy
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/total", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String,Object> total(HttpServletRequest request,gxbuildapply gxbuildpy,HttpServletResponse response, HttpSession session ){
		//
		int items =0;
		int totolAlreadyDeal = 0;
		int totalUntreated =0;
		Page page =null;
		PageModel pm = gxbuildservice.findByPage(page,gxbuildpy);
		
		items =pm.getTotalCount();
		String items2= String.valueOf(items);
		String totolAlreadyDeal2=String.valueOf(totolAlreadyDeal);
		String totalUntreated2 =String.valueOf(totalUntreated);
		
		Map<String,Object> map = new HashMap<>();
		map.put("items2", items2);
		map.put("totolAlreadyDeal2",totolAlreadyDeal2);
		map.put("totalUntreated2", totalUntreated2);
		totolAlreadyDeal =0;
		items =0;
		totalUntreated =0;
		return map;
	}
	/**
	 * 跳转修改页面
	 * @param id
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/edit/{id}",method ={RequestMethod.GET,RequestMethod.POST})
	public String edit(@PathVariable("id") String id,Model model,HttpSession session){
		gxbuildapply(id,model);
		return "tiles.gxbuild.bodyAdd";
	}
	/**
	 * 查询封装数据
	 * @param id
	 * @param model
	 */
	private void gxbuildapply(String id,Model model){
		gxbuildapply gxbuildappl=gxbuildservice.findById(id);
		model.addAttribute("gxbuildappl",gxbuildappl);
	}
	
	/**
	 * 上报
	 * @param id
	 * @param session
	 * @return
	 */
	@RequestMapping(value ="/Report")
	@ResponseBody
	public Map<String,Object> Report(String id,HttpSession session){
		
		Map<String,Object>modelMap =new HashMap<>();
		String name = (String)session.getAttribute("userCode");
		if(name != null){
			Map<String,Object> res = new HashMap<>();
			gxbuildapply gxbuildapp=new gxbuildapply();
			
			gxbuildapp.setIssubmit("1");
			gxbuildapp.setId(id);
			res =gxbuildservice.IsSubmit(gxbuildapp);
			modelMap.put("result", res.get("state"));
		}else{
			modelMap.put("result", "failure");
		}
		
		return modelMap;
	}
	
	//---------------------------------1.2审批页面--------------------------------------------------------------------------------------------------
	
	/**
	 * 建设1.2审批
	 * @param model
	 * @param request
	 * @param session
	 * @param tbprojectinfo
	 * @return
	 */
	@RequestMapping(value = "/indexTwo",method ={RequestMethod.GET,RequestMethod.POST})
	public String viewtwo(Model model, HttpServletRequest request, HttpSession session) {
		// 用户id
		String proviceId = "";
		String cityId = "";
		String countryId = "";
		// 用户类型
		String userLevelId = (String) session.getAttribute("userLevelId");
		if (userLevelId != null && !userLevelId.equals("")) {
			// 市级用户
			if (userLevelId.equals("3")) {
				proviceId = (String) session.getAttribute("provinceId");
				cityId = (String) session.getAttribute("cityId");
				// 县级
			} else if (userLevelId.equals("4")) {
				countryId = (String) session.getAttribute("countyId");
				proviceId = (String) session.getAttribute("provinceId");
				cityId = (String) session.getAttribute("cityId");
			}
		}
		// 省
		Tdprovince tdadmindiv = tdprovinceService.selectByPrimaryKey(proviceId);
		// 市
		Tdadmindiv tdadmindiv1 = divService.selectByPrimaryKey(cityId);
		// 县
		Tdadmindiv tdadmindiv2 = divService.selectByPrimaryKey(countryId);
		
		model.addAttribute("tdadmindiv", tdadmindiv);
		model.addAttribute("tdadmindiv1", tdadmindiv1);
		model.addAttribute("tdadmindiv2", tdadmindiv2);
		return "tiles.gxbuild.bodytwo";
	}
	
	/**
	 * 查询待处理
	 * @param page
	 * @param request
	 * @param gxbuildpy
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/listTwo", method = { RequestMethod.GET, RequestMethod.POST })
	public PageModel listTwo(Page page,HttpServletRequest request,gxbuildapply gxbuildpy, HttpSession session ){
		String usercode = (String) session.getAttribute("userCode");
		PageModel pm = gxbuildservice.findPending(page,gxbuildpy,usercode);
		return pm;
	}
	
	/**
	 * 审批详情页
	 * @param id
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/editTwo/{id}",method ={RequestMethod.GET,RequestMethod.POST})
	public String editTwo(@PathVariable("id") String id,Model model,HttpSession session){
		gxbuildapply(id,model);
		return "tiles.gxbuild.bodyAddTwo";
		
	}
	/**
	 * 审核
	 * @param request
	 * @param session
	 * @param gxbuildapply
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/examine", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String,Object> examine(HttpServletRequest request,HttpSession session, gxbuildapply gxbuildapply,int ispass)throws Exception{
		//创建一个map用于返回
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//获取session数据
		String usercode = (String)session.getAttribute("userCode");//用于 创建人
		if(usercode != null){
			Map<String,Object> res = new HashMap<String,Object>();
			res=gxbuildservice.examine(gxbuildapply,usercode,ispass);
			modelMap.put("result", res.get("state"));
		}else{
			modelMap.put("result", "failure");
		}
		return modelMap;
	}
	
	
	
	//-------------1.3修改界面-------------------------------------------------------------------------------------------------
	//UpdateBuild
	/**
	 * 建设1.3修改
	 * @param model
	 * @param request
	 * @param session
	 * @param 
	 * @return
	 */
	@RequestMapping(value = "/UpdateBuild",method ={RequestMethod.GET,RequestMethod.POST})
	public String UpdateBuild(Model model, HttpServletRequest request, HttpSession session) {
		// 用户id
		String proviceId = "";
		String cityId = "";
		String countryId = "";
		// 用户类型
		String userLevelId = (String) session.getAttribute("userLevelId");
		if (userLevelId != null && !userLevelId.equals("")) {
			// 市级用户
			if (userLevelId.equals("3")) {
				proviceId = (String) session.getAttribute("provinceId");
				cityId = (String) session.getAttribute("cityId");
				// 县级
			} else if (userLevelId.equals("4")) {
				countryId = (String) session.getAttribute("countyId");
				proviceId = (String) session.getAttribute("provinceId");
				cityId = (String) session.getAttribute("cityId");
			}
		}
		// 省
		Tdprovince tdadmindiv = tdprovinceService.selectByPrimaryKey(proviceId);
		// 市
		Tdadmindiv tdadmindiv1 = divService.selectByPrimaryKey(cityId);
		// 县
		Tdadmindiv tdadmindiv2 = divService.selectByPrimaryKey(countryId);
		model.addAttribute("tdadmindiv", tdadmindiv);
		model.addAttribute("tdadmindiv1", tdadmindiv1);
		model.addAttribute("tdadmindiv2", tdadmindiv2);
		return "tiles.gxbuild.bodythree";
	}
	/**
	 * 查询主体表
	 * @param page
	 * @param request
	 * @param gxbuildpy
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/listThree", method = { RequestMethod.GET, RequestMethod.POST })
	public PageModel listThree(Page page,HttpServletRequest request,gxbuild gxbuild, HttpSession session ){
		String usercode = (String) session.getAttribute("userCode");
		
		PageModel pm = gxbuildservice.selectBuild(page,gxbuild,usercode);
		return pm;
	}
	
	/**
	 * 跳转修改页面-1.3修改模块
	 * @param id
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/editThree/{id}",method ={RequestMethod.GET,RequestMethod.POST})
	public String editThree(@PathVariable("id") String id,Model model,HttpSession session){
		gxbuild(id,model);
		return "tiles.gxbuild.bodyAddthree";
	}
	/**
	 * 查询封装数据-1.3修改模块
	 * @param id
	 * @param model
	 */
	private void gxbuild(String id,Model model){
		gxbuild gxbuild = gxbuildservice.selectById(id);
		model.addAttribute("gxbuild",gxbuild);
	}
	
	/**
	 * 添加基本信息-1.3保存修改信息
	 * @param request
	 * @param session
	 * @param gxbuildapply
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/savebuu",method ={RequestMethod.GET,RequestMethod.POST})
	public Map<String,Object> savebuu(HttpServletRequest request,HttpSession session, gxbuild gxbui)throws Exception{
		//创建一个map用于返回
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//获取session数据
		String usercode = (String)session.getAttribute("userCode");//用于 创建人
		//省市县
		String provinceid = (String)session.getAttribute("provinceId");//省
		String cityid = (String)session.getAttribute("cityId");//市
		String countyid = (String)session.getAttribute("countyId");//县
		if(usercode != null){
			gxbui.setCountyid(countyid);//县
			gxbui.setCityid(cityid);
			gxbui.setProvinceid(provinceid);
			
			Map<String,Object> res = new HashMap<String,Object>();
			res=gxbuildservice.UpdateBuild(gxbui,usercode);
			modelMap.put("result", res.get("state"));
		}else{
			modelMap.put("result", "failure");
		}
		return modelMap;
	}
	//============================分割 1.4查询主表信息
	/**
	 * 建设1.4查询
	 * @param model
	 * @param request
	 * @param session
	 * @param 
	 * @return
	 */
	@RequestMapping(value = "/listbuild",method ={RequestMethod.GET,RequestMethod.POST})
	public String listbuild(Model model, HttpServletRequest request, HttpSession session) {
		// 用户id
		String proviceId = "";
		String cityId = "";
		String countryId = "";
		// 用户类型
		String userLevelId = (String) session.getAttribute("userLevelId");
		if (userLevelId != null && !userLevelId.equals("")) {
			// 市级用户
			if (userLevelId.equals("3")) {
				proviceId = (String) session.getAttribute("provinceId");
				cityId = (String) session.getAttribute("cityId");
				// 县级
			} else if (userLevelId.equals("4")) {
				countryId = (String) session.getAttribute("countyId");
				proviceId = (String) session.getAttribute("provinceId");
				cityId = (String) session.getAttribute("cityId");
			}
		}
		// 省
		Tdprovince tdadmindiv = tdprovinceService.selectByPrimaryKey(proviceId);
		// 市
		Tdadmindiv tdadmindiv1 = divService.selectByPrimaryKey(cityId);
		// 县
		Tdadmindiv tdadmindiv2 = divService.selectByPrimaryKey(countryId);
		model.addAttribute("tdadmindiv", tdadmindiv);
		model.addAttribute("tdadmindiv1", tdadmindiv1);
		model.addAttribute("tdadmindiv2", tdadmindiv2);
		return "tiles.gxbuild.bodyfour";
	}
	/**
	 * 跳转修改页面-1.4查询模块
	 * @param id
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/editfour/{id}",method ={RequestMethod.GET,RequestMethod.POST})
	public String editfour(@PathVariable("id") String id,Model model,HttpSession session){
		gxbuild(id,model);
		return "tiles.gxbuild.bodyAddfour";
	}
	
	
	
	
}
