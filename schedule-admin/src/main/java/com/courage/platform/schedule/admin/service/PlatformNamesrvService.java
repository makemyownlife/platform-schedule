package com.courage.platform.schedule.admin.service;

import com.courage.platform.schedule.dao.PlatformNamesrvDao;
import com.courage.platform.schedule.dao.domain.PlatformNamesrv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyong on 2019/11/12.
 */
@Service
public class PlatformNamesrvService {

    private volatile List<PlatformNamesrv> cache = null;

    @Autowired
    private PlatformNamesrvDao platformNamesrvDao;

    public PlatformNamesrv getPlatformNamesrvByNamesrvIp(String namesrvIp) {
        return platformNamesrvDao.getPlatformNamesrvByNamesrvIp(namesrvIp);
    }

    public List<PlatformNamesrv> findAll() {
        return platformNamesrvDao.findAll();
    }

    //每30s加载一次任务信息
    @Scheduled(initialDelay = 0, fixedRate = 30000)
    @PostConstruct
    public void load() {
        List<PlatformNamesrv> list = platformNamesrvDao.findAll();
        cache = list;
    }

    public List<PlatformNamesrv> getCache() {
        return cache;
    }

    public List<PlatformNamesrv> getPage(Map param, String start, Integer pageSize) {
        param.put("start", Integer.valueOf(start));
        param.put("pageSize", pageSize);
        return platformNamesrvDao.findPage(param);
    }

    public Integer count(Map param) {
        return platformNamesrvDao.count(param);
    }

    public void insert(Map param) {
        platformNamesrvDao.insert(param);
    }

    public PlatformNamesrv getById(Long id) {
        return platformNamesrvDao.getById(id);
    }

    public void update(Map param) {
        platformNamesrvDao.update(param);
    }

    public void delete(Map param) {
        platformNamesrvDao.delete(param);
    }

}
