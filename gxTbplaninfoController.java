package com.casic.web.controller.guanxian;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
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

import com.alibaba.druid.filter.AutoLoad;
import com.casic.model.Page;
import com.casic.model.Tdadmindiv;
import com.casic.model.Tdprovince;
import com.casic.model.TdprovinceExample;
import com.casic.model.guanxian.gxplanapply;
import com.casic.model.guanxian.gxplanapplyExample;
import com.casic.service.guanxian.gxTbplaninfoApplyService;
import com.casic.vo.PageModel;
import com.casic.vo.Search;
 
/**
 * 管线规划的controller层
 * @author szc
 *
 */
@Controller
@RequestMapping("/gxplaninfo")
public class gxTbplaninfoController {
	@Autowired
	 public gxTbplaninfoApplyService service;
	@InitBinder
	protected void initBinder(WebDataBinder binder){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	/*// 返回合计
		@ResponseBody
		@RequestMapping(value = "/total", method = { RequestMethod.POST,RequestMethod.GET })
		public List total(HttpServletRequest request, HttpServletResponse response,Model model,
				HttpSession session) throws IOException {
			search=getSearch(request, model, session, search, response);

		List<String> list = planService.total(search);
			List<String> total=new ArrayList<String>();
			DecimalFormat df = new DecimalFormat("#,##0.00");//设置格式
			//将集合中的字符串 转换成double类型  第一个忽略不转换
			for(int i=0;i<list.size();i++){
				if(i==0){
					total.add(list.get(0));
				}else{				
					total.add(df.format(Double.valueOf(list.get(i))));
				}
			}
			return total;
		}*/
	/**
	 * 规划数据展现
	 * @param page
	 * @param request
	 * @param model
	 * @param session
	 * @param search
	 * @param response
	 * @return
	 * @throws IOException
	 */
		@ResponseBody
		@RequestMapping(value = "/list", method = { RequestMethod.GET,
				RequestMethod.POST })
		public PageModel list(Page page, HttpServletRequest request, Model model,
				HttpSession session, HttpServletResponse response)
				throws IOException {
			gxplanapply example=new gxplanapply();
			String proviceId="";
			String cityId="";
			String countryId="";
			//用户类型
			String userLevelId=(String) session.getAttribute("userLevelId");
			String userTypeId=(String)session.getAttribute("userTypeId");
			if(userLevelId!=null&& !userLevelId.equals("")){
				//市级用户
				if(userLevelId.equals("3")){
					proviceId=(String) session.getAttribute("provinceId");
					cityId=(String) session.getAttribute("cityId");
				//县级
				}else if(userLevelId.equals("4")){
					countryId=(String) session.getAttribute("countyId");
					proviceId=(String) session.getAttribute("provinceId");
					cityId=(String) session.getAttribute("cityId");
				}
			}
			String planname=request.getParameter("planname");
			example.setProvinceid(proviceId);
			example.setCityid(cityId);
			example.setCountyid(countryId);
			PageModel pm = service.findPlanByPage(page, example);
			return null;
		}
	/**
	 * 获取规划的填报点击初始界面
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value= "/gettbplanview", method = {RequestMethod.GET,RequestMethod.POST})
	public String getplaninfoview(Model model,HttpServletRequest request ,HttpSession session ){
		gxplanapplyExample  gxplan=new gxplanapplyExample();
		//用户id
				String proviceId="";
				String cityId="";
				String countryId="";
				//用户类型
				String userLevelId=(String) session.getAttribute("userLevelId");
				String userTypeId=(String)session.getAttribute("userTypeId");
				if(userLevelId!=null&& !userLevelId.equals("")){
					//市级用户
					if(userLevelId.equals("3")){
						proviceId=(String) session.getAttribute("provinceId");
						cityId=(String) session.getAttribute("cityId");
					//县级
					}else if(userLevelId.equals("4")){
						countryId=(String) session.getAttribute("countyId");
						proviceId=(String) session.getAttribute("provinceId");
						cityId=(String) session.getAttribute("cityId");
					}
				}
			gxplan.createCriteria().andProvinceidEqualTo(proviceId);
			gxplan.createCriteria().andCityidEqualTo(cityId);
			gxplan.createCriteria().andCountyidEqualTo(countryId);
			/*List<gxplanapply> list=service.selectbypcc(gxplan);
			model.addAttribute("gxplaninfo",list);*/
		return "tiles.guanxian.gxplaninfo.getview";
	}
	/**
	 * 规划初始界面点击之后跳转的新增界面
	 * 新增界面从前台session中获取相关信息在展示在前台，校准数据的准确性
	 * @param model
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value= "/gettbplanaddview", method = {RequestMethod.GET,RequestMethod.POST})
	public String getAddview(Model model,HttpServletRequest request ,
			HttpSession session ){
		//用户id
		String provinceId="";
		String cityId="";
		String countryId="";
		String userId="";
		String usercode="";
		//用户类型
		userId=(String)session.getAttribute("userId");
		usercode=(String)session.getAttribute("userCode");
		String userLevelId=(String) session.getAttribute("userLevelId");
		String userTypeId=(String)session.getAttribute("userTypeId");
		if(userLevelId!=null&& !userLevelId.equals("")){
			//市级用户
			if(userLevelId.equals("3")){
				provinceId=(String) session.getAttribute("provinceId");
				cityId=(String) session.getAttribute("cityId");
			//县级
			}else if(userLevelId.equals("4")){
				countryId=(String) session.getAttribute("countyId");
				provinceId=(String) session.getAttribute("provinceId");
				cityId=(String) session.getAttribute("cityId");
			}
		}
		model.addAttribute("provinceId",provinceId);
		model.addAttribute("cityId",cityId);
		model.addAttribute("countyId",countryId);
		model.addAttribute("userId",userId);
		model.addAttribute("userCode",usercode);
		return "tiles.guanxian.gxplaninfo.addview";
	}
	/**
	 * 管线规划信息保存和更新走的controller
	 * @param model
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value= "/gxplaninfosave", 
			method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Boolean save(Model model,HttpServletRequest request ,gxplanapply apply,
			HttpSession session,HttpServletResponse response){
		Map<String,Integer> map=new HashMap<String,Integer>();
		//获取session数据
				String usercode = (String)session.getAttribute("userCode");//用于 创建人
				//省市县
				String provinceid = (String)session.getAttribute("provinceId");//省
				String cityid = (String)session.getAttribute("cityId");//市
				String countyid = (String)session.getAttribute("countyId");//县
				/*boolean a=false;
				if(usercode != null){
					apply.setCountyid(countyid);//县
					apply.setCityid(cityid);
					apply.setProvinceid(provinceid);
					if(apply.getId()==null||"".equals(apply.getId())){
						 a=service.insert(apply);
					}else{
						a=service.insert(apply);
					}
					map.put("result", 2);
				}else{
					map.put("result", 1);
				}*/
				return true;
		
	}
	/**
	 * 管线规划信息中上报所走的方法
	 * @param model
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value= "/gxplaninfosubmit", 
			method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Integer> submit(Model model,HttpServletRequest request ,
			HttpSession session,HttpServletResponse response){
		Map<String,Integer> map=new HashMap<String,Integer>();
				return map;
		
	}
	/**
	 * 管线规划信息保存的信息点击修改之后进入的界面的跳转方法
	 * @param model
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value= "/gxplaninfosaveedit", 
			method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String saveEdit(Model model,HttpServletRequest request ,
			HttpSession session,HttpServletResponse response){
				return "tiles.guanxian.gxplaninfo.editview";
		
	}
	/**
	 * 管线规划信息上报后的信息点击修改之后进入的界面的跳转方法
	 * @param model
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value= "/gxplaninfosubmitedit", 
			method = {RequestMethod.GET,RequestMethod.POST})
	public String submitEdit(Model model,HttpServletRequest request ,
			HttpSession session,HttpServletResponse response){
		
				return "tiles.guanxian.gxplaninfo.submitEditview";
		
	}
	/**
	 * 管线规划信息删除方法，传入id进行物理删除
	 * @param id
	 * @param model
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value= "/gxplaninfodelete/{id}", 
			method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public boolean delete(@PathVariable("id")String id,Model model,HttpServletRequest request ,
			HttpSession session,HttpServletResponse response){
		gxplanapplyExample example=new gxplanapplyExample();
		if(id!=null && id!=""){
			example.createCriteria().andIdEqualTo(id);
		}
				return service.deletebyExample(example);
		
	}
	/**
	 * 管线规划查看方法，传入id，查询实体类返回
	 * @param id
	 * @param model
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value= "/gxplaninread/{id}", 
			method = {RequestMethod.GET,RequestMethod.POST})
	public String getRead(@PathVariable("id")String id,Model model,HttpServletRequest request ,
			HttpSession session,HttpServletResponse response){
		/*gxplanapplyExample example=new gxplanapplyExample();
		if(id!=null && id!=""){
			example.createCriteria().andIdEqualTo(id);
		}
		List<gxplanapply> ls=service.selectbypcc(example);
		if(ls.size()==1){
			model.addAttribute("plan",ls.get(0));
		}*/
		return "tiles.guanxian.gxplaninfo.readview";
		
	}
	/**
	 * 管线审核用户进入界面刚刚进入的界面查询出来的列表
	 * @param model
	 * @param request
	 * @param session
	 * @param response
	 * @return
	 */
	@RequestMapping(value= "/gxplanshenhe", method = {RequestMethod.GET,RequestMethod.POST})
	public String getshenhe(Model model,HttpServletRequest request , HttpSession session,HttpServletResponse response){
		/*gxplanapply apply=new gxplanapply();
		//获取session数据
		String usercode = (String)session.getAttribute("userCode");//用于 创建人
		//省市县
		String provinceid = (String)session.getAttribute("provinceId");//省
		String cityid = (String)session.getAttribute("cityId");//市
		String countyid = (String)session.getAttribute("countyId");//县
		if(usercode != null){
			apply.setCountyid(countyid);//县
			apply.setCityid(cityid);
			apply.setProvinceid(provinceid);
		}
		System.out.println("tiles.guanxian.gxplaninfo.shenhesearchview");*/
		return "tiles.guanxian.gxplaninfo.shenhe";
		
	}
	/**
	 * 查询用户进入这个方法进行查询
	 * 
	 * @return
	 */
	@RequestMapping(value= "/gxplansearch", method = {RequestMethod.GET,RequestMethod.POST})
	public String getsearch(Model model,HttpServletRequest request , HttpSession session,HttpServletResponse response){
		return "tiles.guanxian.gxplaninfo.submitedallsearch";
		
	}
}
