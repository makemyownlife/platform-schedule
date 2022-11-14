package com.courage.platform.schedule.admin.service;

import com.courage.platform.schedule.admin.util.Md5Util;
import com.courage.platform.schedule.dao.AppinfoDao;
import com.courage.platform.schedule.dao.domain.AppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zhangyong on 2019/11/5.
 */
@Service
public class AppInfoService {

    private static final Logger logger = LoggerFactory.getLogger(AppInfoService.class);

    private static final int APP_ID_STEP = 1400000;

    @Autowired
    private AppinfoDao appinfoDao;

    public List<AppInfo> getAll() {
        return appinfoDao.findAll();
    }

    public AppInfo getByAppKey(String appKey) {
        return appinfoDao.findAppinfoByAppKey(appKey);
    }

    public AppInfo getById(String id) {
        return appinfoDao.findAppinfoById(id);
    }

    public List<AppInfo> getPage(Map param, String start, Integer pageSize) {
        param.put("start", Integer.valueOf(start));
        param.put("pageSize", pageSize);
        return appinfoDao.findPage(param);
    }

    public Integer count(Map param) {
        return appinfoDao.count(param);
    }

    public Integer getMaxAppKey() {
        Integer maxAppKey = appinfoDao.getMaxAppKey();
        if (maxAppKey == null) {
            return APP_ID_STEP + 1;
        }
        return maxAppKey + 1;
    }

    public void addAppInfo(AppInfo appinfo) {
        appinfo.setAppKey(String.valueOf(getMaxAppKey()));
        appinfo.setAppSecret(Md5Util.getMd5Code(appinfo.getAppName() + UUID.randomUUID().toString()));
        appinfo.setCreateTime(new Date());
        appinfo.setUpdateTime(new Date());
        appinfoDao.insertAppInfo(appinfo);
    }

    public void update2(AppInfo appinfo) {
        appinfoDao.update2(appinfo);
    }

}
