package com.courage.platform.schedule.worker.service.recovery;

import com.alibaba.fastjson.JSON;
import com.courage.platform.schedule.dao.ScheduleJobLogDao;
import com.courage.platform.schedule.dao.domain.ScheduleJobLog;
import com.courage.platform.schedule.rpc.protocol.CallbackCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ScheduleLogRecoveryAction implements RecoveryAction {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleLogRecoveryAction.class);

    @Autowired
    private ScheduleJobLogDao scheduleJobLogDao;

    @Override
    public boolean doAction(RecoveryMessage recoveryMessage) {
        CallbackCommand callbackCommand = JSON.parseObject(recoveryMessage.getJson(), CallbackCommand.class);
        Long jobLogId = Long.valueOf(callbackCommand.getJobLogId());
        //本地缓存中还有数据 则说明 insert并未结束 则需要用延迟存储来实现
        ScheduleJobLog scheduleJobLog = (ScheduleJobLog) RecoveryLruCache.get(jobLogId);
        if (scheduleJobLog != null) {
            logger.info("日志id:" + scheduleJobLog.getId() + "没有入库,通过延迟存储来处理");
            return false;
        }
        //修改log状态
        Map map = new HashMap<>();
        map.put("id", jobLogId);
        map.put("callbackMessage", callbackCommand.getHandleMsg());
        map.put("callbackTime", callbackCommand.getHandleTime());
        map.put("callbackStatus", callbackCommand.getHandleCode());
        logger.info("callbackmap :" + map);
        scheduleJobLogDao.updateCallback(map);
        return true;
    }


}
