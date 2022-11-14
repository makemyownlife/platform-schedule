package com.courage.platform.schedule.admin.controller;

import com.courage.platform.schedule.admin.mvc.PermissionLimit;
import com.courage.platform.schedule.admin.service.AppInfoService;
import com.courage.platform.schedule.admin.service.PlatformNamesrvService;
import com.courage.platform.schedule.admin.service.ScheduleJobInfoService;
import com.courage.platform.schedule.admin.util.Md5Util;
import com.courage.platform.schedule.dao.domain.AppInfo;
import com.courage.platform.schedule.dao.domain.PlatformNamesrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/*
   应用管理
 */
@Controller
public class AppController {

    private final static Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private PlatformNamesrvService platformNamesrvService;

    @Autowired
    private ScheduleJobInfoService scheduleJobInfoService;

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

        List<AppInfo> list = appInfoService.getPage(param, start, Integer.valueOf(length));
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
        try {
            String appName = httpServletRequest.getParameter("appName");
            String remark = httpServletRequest.getParameter("remark");
            logger.info("appName:{} remark:{}", new Object[]{appName, remark});

            AppInfo appinfo = new AppInfo();
            appinfo.setAppName(appName);
            appinfo.setRemark(remark);
            appinfo.setAppKey(Md5Util.getMd5Code(appName + UUID.randomUUID().toString()));

            appInfoService.addAppInfo(appinfo);

            Map map = new HashMap();
            map.put("code", "200");
            return map;
        } catch (Exception e) {
            logger.error("doAdd error:", e);
            Map map = new HashMap();
            map.put("code", "500");
            return map;
        }
    }

    //编辑页面
    @RequestMapping("/applist/updateapppage")
    public String updateapppage(HttpServletRequest httpServletRequest, Model model) {
        String id = httpServletRequest.getParameter("id");
        AppInfo appinfo = appInfoService.getById(id);
        model.addAttribute("appinfo", appinfo);
        return "appinfo/appupdate";
    }

    @RequestMapping("/applist/doUpdate")
    @ResponseBody
    public Map doUpdate(HttpServletRequest httpServletRequest) {
        String id = httpServletRequest.getParameter("id");
        String appName = httpServletRequest.getParameter("appName");
        String remark = httpServletRequest.getParameter("remark");
        logger.info("appName:{} remark:{}", new Object[]{appName, remark});

        AppInfo appinfo = new AppInfo();
        appinfo.setId(Integer.valueOf(id));
        appinfo.setAppName(appName);
        appinfo.setRemark(remark);
        appinfo.setUpdateTime(new Date());

        appInfoService.update2(appinfo);

        Map map = new HashMap();
        map.put("code", "200");
        return map;
    }

    @RequestMapping("/onlineapp")
    @PermissionLimit(limit = true)
    public String onlineapp(HttpServletRequest httpServletRequest, Model model) {
        List<PlatformNamesrv> platformNamesrvList = platformNamesrvService.findAll();
        model.addAttribute("platformNamesrvList", platformNamesrvList);
        return "appinfo/onlineapp";
    }

    @RequestMapping("/onlineapp/pageList")
    @PermissionLimit(limit = true)
    @ResponseBody
    public Map<String, Object> onlinepageList(HttpServletRequest httpServletRequest) {
        String start = httpServletRequest.getParameter("start");
        String length = httpServletRequest.getParameter("length"); //类似请求pageSize
        String appName = httpServletRequest.getParameter("appName");
        String namesrvIp = httpServletRequest.getParameter("namesrvIp");

        Map<String, String> param = new HashMap<>();
        param.put("start", start);
        param.put("length", length);
        param.put("appName", appName);
        param.put("namesrvIp", namesrvIp);
        logger.info("onlineapp param:" + param);

        Map map = scheduleJobInfoService.onlineApp(namesrvIp, appName, start, length);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", map.get("totalCount"));        // 总记录数
        maps.put("recordsFiltered", map.get("totalCount"));        // 总记录数
        maps.put("data", map.get("data"));                    // 分页列表
        return maps;
    }


}
