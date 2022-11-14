package com.courage.platform.schedule.admin.api;

import com.courage.platform.schedule.admin.mvc.PermissionLimit;
import com.courage.platform.schedule.admin.service.PlatformNamesrvService;
import com.courage.platform.schedule.dao.domain.PlatformNamesrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
   应用管理
 */
@RestController
public class ApiController {

    private final static Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private PlatformNamesrvService platformNamesrvService;

    @RequestMapping("/address")
    @PermissionLimit(limit = false)
    public Object scheduleserver() {
        Map map = new HashMap<>();
        map.put("code", 0);

        List<PlatformNamesrv> data = platformNamesrvService.getCache();

        map.put("data", data);

        return map;
    }

}
