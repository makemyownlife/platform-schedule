package com.courage.platform.schedule.admin.controller;

import com.courage.platform.schedule.admin.service.PlatformNamesrvService;
import com.courage.platform.schedule.dao.domain.PlatformNamesrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyong on 2019/11/13.
 */
@Controller
public class ClusterController {

    private final static Logger logger = LoggerFactory.getLogger(ClusterController.class);

    @Autowired
    private PlatformNamesrvService platformNamesrvService;

    @RequestMapping("/cluster")
    public String cluster() {
        return "cluster/cluster.index";
    }

    @RequestMapping("/cluster/pageList")
    @ResponseBody
    public Map<String, Object> appPageList(HttpServletRequest httpServletRequest) {
        String start = httpServletRequest.getParameter("start");
        //类似请求pageSize
        String length = httpServletRequest.getParameter("length");

        Map<String, String> param = new HashMap<>();
        param.put("start", start);
        param.put("length", length);

        List<PlatformNamesrv> list = platformNamesrvService.getPage(param, start, Integer.valueOf(length));
        Integer count = platformNamesrvService.count(param);

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", count);        // 总记录数
        maps.put("recordsFiltered", count);        // 总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @RequestMapping("/cluster/addpage")
    public String addpage() {
        return "cluster/addpage";
    }

    @RequestMapping("/cluster/updatepage")
    public String updatepage(HttpServletRequest request, Model model) {
        String id = request.getParameter("id");
        PlatformNamesrv platformNamesrv = platformNamesrvService.getById(Long.valueOf(id));
        model.addAttribute("platformNamesrv", platformNamesrv);
        return "cluster/updatepage";
    }

    @RequestMapping("/cluster/doAdd")
    @ResponseBody
    public Map doAdd(HttpServletRequest httpServletRequest) {
        String namesrvIp = httpServletRequest.getParameter("namesrvIp");
        String status = httpServletRequest.getParameter("status");
        Map map = new HashMap();
        map.put("namesrvIp", namesrvIp);
        map.put("status", status);

        platformNamesrvService.insert(map);

        Map result = new HashMap();
        result.put("code", "200");
        return result;
    }

    @RequestMapping("/cluster/doUpdate")
    @ResponseBody
    public Map doUpdate(HttpServletRequest httpServletRequest) {
        String namesrvIp = httpServletRequest.getParameter("namesrvIp");
        String status = httpServletRequest.getParameter("status");
        String id = httpServletRequest.getParameter("id");
        Map map = new HashMap();
        map.put("namesrvIp", namesrvIp);
        map.put("status", status);
        map.put("id", id);

        platformNamesrvService.update(map);

        Map result = new HashMap();
        result.put("code", "200");
        return result;
    }

    @RequestMapping("/cluster/delete")
    @ResponseBody
    public Map delete(HttpServletRequest httpServletRequest) {
        String id = httpServletRequest.getParameter("id");
        Map map = new HashMap();
        map.put("id", id);

        platformNamesrvService.delete(map);

        Map result = new HashMap();
        result.put("code", "200");
        return result;
    }


}
