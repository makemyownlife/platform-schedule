package com.courage.platform.schedule.worker.service.distribute;

import com.courage.platform.schedule.dao.domain.ScheduleJobInfo;
import com.courage.platform.schedule.worker.service.PlatformNamesrvService;
import com.courage.platform.schedule.worker.service.ScheduleJobExecutor;
import com.courage.platform.schedule.worker.service.ScheduleJobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service("baseDistribute")
public class BaseDistribute {

    private volatile boolean isLeader = false;

    @Autowired
    private ScheduleJobInfoService scheduleJobInfoService;

    @Autowired
    private PlatformNamesrvService platformNamesrvService;

    @Autowired
    private ScheduleJobExecutor scheduleJobExecutor;

    public void startJobs() {
        Map<Long, ScheduleJobInfo> jobInfoMap = scheduleJobInfoService.getJobInfoCache();
        Iterator<Map.Entry<Long, ScheduleJobInfo>> entries = jobInfoMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Long, ScheduleJobInfo> entry = entries.next();
            ScheduleJobInfo scheduleJobInfo = entry.getValue();
            scheduleJobExecutor.scheduleJob(scheduleJobInfo.getId());
        }
    }

    public void destroyJobs() {
        scheduleJobExecutor.removeJobs();
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

}
