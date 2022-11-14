package com.courage.platform.schedule.admin.service;

import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingCommand;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingSerializable;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingSysResponseCode;
import com.courage.platform.schedule.dao.ScheduleJobInfoDao;
import com.courage.platform.schedule.dao.domain.PlatformNamesrv;
import com.courage.platform.schedule.dao.domain.ScheduleJobInfo;
import com.courage.platform.schedule.rpc.ScheduleRpcClient;
import com.courage.platform.schedule.rpc.protocol.BaseCommand;
import com.courage.platform.schedule.rpc.protocol.CommandEnum;
import com.courage.platform.schedule.rpc.protocol.ConsoleOnlineAppCommand;
import com.courage.platform.schedule.rpc.protocol.ConsoleTriggerCommand;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyong on 2019/11/5.
 */
@Service
public class ScheduleJobInfoService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleJobInfoService.class);

    @Autowired
    private ScheduleJobInfoDao scheduleJobInfoDao;

    @Autowired
    private PlatformNamesrvService platformNamesrvService;

    private ScheduleRpcClient scheduleRpcClient;

    @PostConstruct
    public void start() {
        this.scheduleRpcClient = new ScheduleRpcClient();
        this.scheduleRpcClient.start();
    }

    public List<ScheduleJobInfo> getPage(Map<String, Object> param, String start, Integer pageSize) {
        param.put("start", Integer.valueOf(start));
        param.put("pageSize", pageSize);
        return scheduleJobInfoDao.findPage(param);
    }

    public Integer count(Map<String, Object> param) {
        return scheduleJobInfoDao.count(param);
    }

    public Integer insert(Map<String, Object> param) {
        return scheduleJobInfoDao.insert(param);
    }

    public ScheduleJobInfo getById(String id) {
        return scheduleJobInfoDao.getById(id);
    }

    public void update(Map map) {
        scheduleJobInfoDao.update(map);
    }

    public void delete(Map map) {
        scheduleJobInfoDao.delete(map);
    }

    public boolean executeAtOnce(String jobId) {
        boolean flag = true;
        logger.info("立刻执行任务id:" + jobId);
        List<PlatformNamesrv> platformNamesrvList = platformNamesrvService.findAll();
        if (CollectionUtils.isNotEmpty(platformNamesrvList)) {
            ConsoleTriggerCommand consoleTriggerCommand = new ConsoleTriggerCommand();
            consoleTriggerCommand.setJobId(Long.valueOf(jobId));
            for (PlatformNamesrv platformNamesrv : platformNamesrvList) {
                try {
                    scheduleRpcClient.send(platformNamesrv.getNamesrvIp(), CommandEnum.CONSOLE_TRIGGER_SCHEDULE_TASK_CMD, consoleTriggerCommand);
                } catch (Throwable throwable) {
                    logger.error("send error:", throwable);
                    flag = false;
                }
            }
        }
        return flag;
    }

    public List<ScheduleJobInfo> findAll() {
        return scheduleJobInfoDao.findAll();
    }

    public Map onlineApp(String namesrvIp, String appName, String start, String length) {
        if (StringUtils.isEmpty(namesrvIp)) {
            Map map = new HashMap();
            map.put("data", Collections.EMPTY_LIST);
            map.put("totalCount", 0);
            return map;
        }
        try {
            ConsoleOnlineAppCommand consoleOnlineAppCommand = new ConsoleOnlineAppCommand();
            consoleOnlineAppCommand.setAppName(StringUtils.trimToEmpty(appName));
            consoleOnlineAppCommand.setPageSize(Integer.valueOf(length));
            consoleOnlineAppCommand.setStart(Integer.valueOf(start));
            PlatformRemotingCommand response = scheduleRpcClient.send(namesrvIp, CommandEnum.CONSOLE_ONLINE_APP_CMD, consoleOnlineAppCommand);
            if (response != null && response.getCode() == PlatformRemotingSysResponseCode.SUCCESS) {
                byte[] body = response.getBody();
                Map map = PlatformRemotingSerializable.decode(body, Map.class);
                return map;
            }
        } catch (Throwable e) {
            logger.error("onlineapp error:", e);
        }
        Map map = new HashMap();
        map.put("data", Collections.EMPTY_LIST);
        map.put("totalCount", 0);
        return map;
    }

    public boolean heartbeat(String namesrvIp) {
        try {
            BaseCommand baseCommand = new BaseCommand() {
            };
            PlatformRemotingCommand response = scheduleRpcClient.send(namesrvIp, CommandEnum.CONSOLE_ONLINE_APP_CMD, baseCommand);
            if (response != null && response.getCode() == PlatformRemotingSysResponseCode.SUCCESS) {
                return true;
            }
        } catch (Throwable e) {
            logger.error("heartbeat error:", e);
        }
        return false;
    }

    @PreDestroy
    public void shutdown() {
        if (this.scheduleRpcClient != null) {
            this.scheduleRpcClient.shutdown();
        }
    }

}
