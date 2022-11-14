package com.courage.platform.schedule.admin.service;

import com.courage.platform.schedule.dao.ScheduleJobLogDao;
import com.courage.platform.schedule.dao.domain.ScheduleJobLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangyong on 2019/11/5.
 */
@Service
public class ScheduleJobLogService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleJobLogService.class);

    @Autowired
    private ScheduleJobLogDao scheduleJobLogDao;

    public List<ScheduleJobLog> getPage(Map<String, Object> param, String start, Integer pageSize) {
        param.put("start", Integer.valueOf(start));
        param.put("pageSize", pageSize);
        return scheduleJobLogDao.findPage(param);
    }

    public Integer count(Map<String, Object> param) {
        return scheduleJobLogDao.count(param);
    }

    public Integer insert(ScheduleJobLog scheduleJobLog) {
        return scheduleJobLogDao.insert(scheduleJobLog);
    }

    public ScheduleJobLog findById(String id) {
        return scheduleJobLogDao.findById(Long.valueOf(id));
    }
    
}
