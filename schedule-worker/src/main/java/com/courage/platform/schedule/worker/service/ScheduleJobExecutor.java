package com.courage.platform.schedule.worker.service;

import com.alibaba.fastjson.JSON;
import com.courage.platform.schedule.core.cron.CronExpression;
import com.courage.platform.schedule.core.util.ThreadFactoryImpl;
import com.courage.platform.schedule.dao.domain.ScheduleJobInfo;
import com.courage.platform.schedule.worker.service.timer.ScheduleHashedWheelTimer;
import com.courage.platform.schedule.worker.service.timer.ScheduleTimeout;
import com.courage.platform.schedule.worker.service.timer.ScheduleTimerTask;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 任务调度执行器
 * Created by zhangyong on 2019/11/4.
 */
@Service
public class ScheduleJobExecutor {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleJobExecutor.class);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(32, 64, 1000 * 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryImpl("JobExecuteThread_"));

    //当前注册的任务
    private ConcurrentHashMap<Long, JobMarker> currentRunningJobs = new ConcurrentHashMap<>(4096);

    private ScheduleHashedWheelTimer scheduleHashedWheelTimer;

    @Autowired
    private ScheduleTriggerService scheduleTriggerService;

    @Autowired
    private ScheduleJobInfoService scheduleJobInfoService;

    @PostConstruct
    public void start() {
        scheduleHashedWheelTimer = new ScheduleHashedWheelTimer(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ScheduleHashedWheelTimer");
            }
        });
        scheduleHashedWheelTimer.start();
    }

    public synchronized void scheduleJob(Long jobId) {
        ScheduleJobInfo scheduleJobInfo = scheduleJobInfoService.getById(jobId);
        if (scheduleJobInfo == null) {
            logger.error("当前任务编号:" + jobId + "无法查询到");
            removeJobById(jobId);
            return;
        }

        boolean addWheelTimer = false;
        JobMarker jobMarker = currentRunningJobs.get(jobId);
        if (jobMarker == null) {
            jobMarker = new JobMarker(scheduleJobInfo.getId(), scheduleJobInfo.getJobCron());
            currentRunningJobs.put(jobId, jobMarker);
            addWheelTimer = true;
        } else {
            if (!jobMarker.getJobCron().equals(scheduleJobInfo.getJobCron())) {
                logger.warn("job编号:" + jobId + " jobName:" + scheduleJobInfo.getJobName() + "cron表达式修改,原cron:" + jobMarker.getJobCron() + " 新:" + scheduleJobInfo.getJobCron());
                jobMarker.setJobCron(scheduleJobInfo.getJobCron());
                addWheelTimer = true;
            }
            if (!jobMarker.getJobAvailable().getId().equals(scheduleJobInfo.getStatus())) {
                logger.warn("job编号:" + jobId + " jobName:" + scheduleJobInfo.getJobName() + "状态修改,原状态:" + jobMarker.getJobAvailable().getId() + " 新:" + scheduleJobInfo.getStatus());
                addWheelTimer = true;
            }
        }

        if (NumberUtils.INTEGER_ZERO.equals(scheduleJobInfo.getStatus())) {
            jobMarker.setJobAvailable(JobAvailable.VALID);
        } else {
            jobMarker.setJobAvailable(JobAvailable.UNVALID);
        }

        if (addWheelTimer) {
            ScheduleTimeout scheduleTimeout = jobMarker.getScheduleTimeout();
            if (scheduleTimeout != null) {
                scheduleTimeout.cancel();
            }
            logger.warn("调度job编号:" + jobId + " jobName:" + scheduleJobInfo.getJobName());
            boolean result = executeJob(jobId);
            if (!result) {
                jobMarker.setJobAvailable(JobAvailable.UNVALID);
            }
        }

    }

    //该方法的前置条件是本地内存有标记
    public boolean executeJob(Long jobId) {
        JobMarker jobMarker = currentRunningJobs.get(jobId);
        if (jobMarker != null && jobMarker.getJobAvailable().equals(JobAvailable.VALID)) {
            ScheduleJobInfo scheduleJobInfo = scheduleJobInfoService.getById(jobId);
            if (scheduleJobInfo == null || !NumberUtils.INTEGER_ZERO.equals(scheduleJobInfo.getStatus())) {
                logger.info("当前任务:" + JSON.toJSONString(scheduleJobInfo) + " 已经禁用!");
                return false;
            }
            Date currentDate = new Date();
            Date nextExecuteDate = null;
            try {
                CronExpression cronExpression = new CronExpression(scheduleJobInfo.getJobCron());
                nextExecuteDate = cronExpression.getNextValidTimeAfter(currentDate);
            } catch (Exception e) {
                logger.error("parse scheduleJobInfo error:", e);
                logger.info("当前任务:" + JSON.toJSONString(scheduleJobInfo) + " cron表达式不合法!");
                return false;
            }
            if (nextExecuteDate != null) {
                long delay = nextExecuteDate.getTime() - currentDate.getTime();
                scheduleJobInfo.setTriggerNextTime(nextExecuteDate);
                ScheduleTimeout scheduleTimeout = scheduleHashedWheelTimer.newTimeout(new ScheduleTimerTask() {
                    @Override
                    public void run(ScheduleTimeout timeout) throws Exception {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //调用rpc触发任务
                                    scheduleTriggerService.doRpcTrigger(jobId);
                                } catch (Throwable e) {
                                    logger.error("doRpcTrigger error:", e);
                                }
                                //计算下一次调度信息
                                executeJob(jobId);
                            }
                        });
                    }
                }, delay, TimeUnit.MILLISECONDS);
                //添加到内存中
                jobMarker.setScheduleTimeout(scheduleTimeout);
            }
        }
        return true;
    }

    public synchronized void removeJobs() {
        Iterator<Map.Entry<Long, JobMarker>> entries = currentRunningJobs.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Long, JobMarker> entry = entries.next();
            JobMarker jobMarker = entry.getValue();
            ScheduleTimeout scheduleTimeout = jobMarker.getScheduleTimeout();
            if (scheduleTimeout != null) {
                scheduleTimeout.cancel();
            }
        }
        currentRunningJobs.clear();
    }

    public synchronized void removeJobById(Long jobId) {
        JobMarker jobMarker = currentRunningJobs.get(jobId);
        if (jobMarker != null) {
            ScheduleTimeout scheduleTimeout = jobMarker.getScheduleTimeout();
            if (scheduleTimeout != null) {
                scheduleTimeout.cancel();
            }
        }
        currentRunningJobs.remove(jobId);
    }

    @PreDestroy
    public void shutdown() {
        try {
            scheduleHashedWheelTimer.stop();
        } catch (Exception e) {
            //
        }
        try {
            executor.shutdown();
        } catch (Exception e) {
            logger.error("shutdown error:", e);
        }
    }

    private enum JobAvailable {

        VALID(0, "有效"), UNVALID(1, "无效");

        private Integer id;
        private String name;

        private JobAvailable(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    private class JobMarker {

        private Long jobId;

        private volatile String jobCron;

        private volatile ScheduleTimeout scheduleTimeout;

        private JobAvailable jobAvailable;

        public JobMarker(Long jobId, String jobCron) {
            this.jobId = jobId;
            this.jobCron = jobCron;
        }

        public Long getJobId() {
            return jobId;
        }

        public void setJobId(Long jobId) {
            this.jobId = jobId;
        }

        public String getJobCron() {
            return jobCron;
        }

        public void setJobCron(String jobCron) {
            this.jobCron = jobCron;
        }

        public ScheduleTimeout getScheduleTimeout() {
            return scheduleTimeout;
        }

        public void setScheduleTimeout(ScheduleTimeout scheduleTimeout) {
            this.scheduleTimeout = scheduleTimeout;
        }

        public JobAvailable getJobAvailable() {
            return jobAvailable;
        }

        public void setJobAvailable(JobAvailable jobAvailable) {
            this.jobAvailable = jobAvailable;
        }

    }

}
