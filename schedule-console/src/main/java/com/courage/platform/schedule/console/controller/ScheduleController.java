package com.courage.platform.schedule.console.controller;

import com.courage.platform.schedule.console.service.AppInfoService;
import com.courage.platform.schedule.console.service.ScheduleJobInfoService;
import com.courage.platform.schedule.console.service.ScheduleJobLogService;
import com.courage.platform.schedule.dao.domain.AppInfo;
import com.courage.platform.schedule.dao.domain.ScheduleJobInfo;
import com.courage.platform.schedule.dao.domain.ScheduleJobLog;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyong on 2019/11/5.
 */
@Controller
public class ScheduleController {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ScheduleJobInfoService scheduleJobInfoService;

    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    @Autowired
    private AppInfoService appInfoService;

    @RequestMapping("/jobinfo")
    public String jobinfo() {
        return "schedule/joblist.index";
    }

    @RequestMapping("/joblog")
    public String loginfo(Model model, HttpServletRequest request) {
        String jobId = request.getParameter("jobId");
        List<ScheduleJobInfo> scheduleJobInfoList = scheduleJobInfoService.findAll();
        model.addAttribute("scheduleJobInfoList", scheduleJobInfoList);
        model.addAttribute("jobId", jobId);
        return "schedule/loglist.index";
    }

    @RequestMapping("/addjobpage")
    public String addjobpage(Model model) {
        List<AppInfo> appInfoList = appInfoService.getAll();
        model.addAttribute("appinfoList", appInfoList);
        return "schedule/jobadd";
    }

    @RequestMapping("/updatejobpage")
    public String updatejobpage(Model model, HttpServletRequest httpServletRequest) {
        String jobId = httpServletRequest.getParameter("id");
        ScheduleJobInfo scheduleJobInfo = scheduleJobInfoService.getById(jobId);
        List<AppInfo> appInfoList = appInfoService.getAll();
        model.addAttribute("appinfoList", appInfoList);
        model.addAttribute("scheduleJobInfo", scheduleJobInfo);
        return "schedule/jobupdate";
    }

    @RequestMapping("/jobinfo/pageList")
    @ResponseBody
    public Map<String, Object> jobinfoPageList(HttpServletRequest httpServletRequest) {
        String start = httpServletRequest.getParameter("start");
        String length = httpServletRequest.getParameter("length"); //类似请求pageSize
        String appName = httpServletRequest.getParameter("appName");
        String jobName = httpServletRequest.getParameter("jobName");
        String jobHandler = httpServletRequest.getParameter("jobHandler");

        Map<String, Object> param = new HashMap<>();
        param.put("start", start);
        param.put("length", length);
        param.put("appName", appName);
        param.put("jobName", jobName);
        param.put("jobHandler", jobHandler);

        List<ScheduleJobInfo> list = scheduleJobInfoService.getPage(param, start, Integer.valueOf(length));

        Integer count = scheduleJobInfoService.count(param);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", count);        // 总记录数
        maps.put("recordsFiltered", count);        // 总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    //添加请求
    @RequestMapping("/addJob")
    @ResponseBody
    public Map addJob(@RequestParam Map<String, Object> params) {
        logger.info("addJob params:" + params);
        String jobCron = (String) params.get("jobCron");
        if (!CronExpression.isValidExpression(jobCron)) {
            Map map = new HashMap();
            map.put("code", "500");
            map.put("msg", "请检查cron表达式");
            return map;
        }
        AppInfo appinfo = appInfoService.getById((String) params.get("appId"));
        params.put("appName", appinfo.getAppName());
        params.put("status", 0);
        scheduleJobInfoService.insert(params);
        Map map = new HashMap();
        map.put("code", "200");
        return map;
    }

    @RequestMapping("/updateJob")
    @ResponseBody
    public Map updateJob(@RequestParam Map<String, Object> params) {
        logger.info("updateJob params:" + params);
        String jobCron = (String) params.get("jobCron");
        if (!CronExpression.isValidExpression(jobCron)) {
            Map map = new HashMap();
            map.put("code", "500");
            map.put("msg", "请检查cron表达式");
            return map;
        }
        AppInfo appinfo = appInfoService.getById((String) params.get("appId"));
        params.put("appName", appinfo.getAppName());
        scheduleJobInfoService.update(params);
        Map map = new HashMap();
        map.put("code", "200");
        return map;
    }

    @RequestMapping("/deleteJob")
    @ResponseBody
    public Map deleteJob(@RequestParam Map<String, Object> params) {
        logger.info("deleteJob params:" + params);
        scheduleJobInfoService.delete(params);
        Map map = new HashMap();
        map.put("code", "200");
        return map;
    }

    @RequestMapping("/validJob")
    @ResponseBody
    public Map validJob(HttpServletRequest httpServletRequest) {
        String id = httpServletRequest.getParameter("id");
        ScheduleJobInfo scheduleJobInfo = scheduleJobInfoService.getById(id);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        if (scheduleJobInfo.getStatus() == 0) {
            params.put("status", 1);
        }
        if (scheduleJobInfo.getStatus() == 1) {
            params.put("status", 0);
        }
        scheduleJobInfoService.update(params);
        Map map = new HashMap();
        map.put("code", "200");
        return map;
    }

    /*
       立刻执行
     */
    @RequestMapping("/executeAtOnce")
    @ResponseBody
    public Map executeAtOnce(HttpServletRequest httpServletRequest) {
        String jobId = httpServletRequest.getParameter("jobId");
        boolean flag = scheduleJobInfoService.executeAtOnce(jobId);
        if (flag) {
            Map map = new HashMap();
            map.put("code", "200");
            return map;
        } else {
            Map map = new HashMap();
            map.put("code", "500");
            return map;
        }
    }

    @RequestMapping("/joblog/pageList")
    @ResponseBody
    public Map<String, Object> joblogPageList(HttpServletRequest httpServletRequest) {
        String start = httpServletRequest.getParameter("start");
        String length = httpServletRequest.getParameter("length"); //类似请求pageSize
        String appName = httpServletRequest.getParameter("appName");
        String logStatus = httpServletRequest.getParameter("logStatus");
        String jobId = httpServletRequest.getParameter("jobId");

        Map<String, Object> param = new HashMap<>();
        param.put("start", start);
        param.put("length", length);
        param.put("appName", appName);
        param.put("logStatus", logStatus);
        param.put("jobId", jobId);

        List<ScheduleJobLog> list = scheduleJobLogService.getPage(param, start, Integer.valueOf(length));

        Integer count = scheduleJobLogService.count(param);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", count);        // 总记录数
        maps.put("recordsFiltered", count);        // 总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

}
