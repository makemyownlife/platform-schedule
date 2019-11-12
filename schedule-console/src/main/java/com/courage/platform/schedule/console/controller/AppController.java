package com.courage.platform.schedule.console.controller;

import com.courage.platform.schedule.console.service.AppInfoService;
import com.courage.platform.schedule.dao.domain.Appinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
   应用管理
 */
@Controller
public class AppController {

    private final static Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppInfoService appInfoService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/applist")
    public String applist() {
        return "appinfo/applist.index";
    }

    @RequestMapping("/applist/pageList")
    @ResponseBody
    public Map<String, Object> appPageList(HttpServletRequest httpServletRequest) {
        String start = httpServletRequest.getParameter("start");
        String length = httpServletRequest.getParameter("length"); //类似请求pageSize
        String appName = httpServletRequest.getParameter("appName");

        Map<String, String> param = new HashMap<>();
        param.put("start", start);
        param.put("length", length);
        param.put("appName", appName);

        List<Appinfo> list = appInfoService.getPage(param, start, Integer.valueOf(length));
        Integer count = appInfoService.count(param);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", count);        // 总记录数
        maps.put("recordsFiltered", count);        // 总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    //添加页面
    @RequestMapping("/applist/addapp")
    public String addAppPage() {
        return "appinfo/appadd";
    }

    //添加请求
    @RequestMapping("/applist/doAdd")
    @ResponseBody
    public Map doAdd(HttpServletRequest httpServletRequest) {
        String appId = httpServletRequest.getParameter("appId");
        String appName = httpServletRequest.getParameter("appName");
        String remark = httpServletRequest.getParameter("remark");
        logger.info("appId:{} appName:{} remark:{}", new Object[]{appId, appName, remark});
        Map map = new HashMap();
        map.put("code", "200");
        return map;
    }

}
