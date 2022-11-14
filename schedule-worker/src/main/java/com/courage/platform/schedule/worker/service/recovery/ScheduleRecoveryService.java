package com.courage.platform.schedule.worker.service.recovery;

import com.alibaba.fastjson.JSON;
import com.courage.platform.schedule.core.util.ThreadFactoryImpl;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务恢复服务(若触发了，但还未入库, 则先存储起来，后续再处理)
 * Created by zhangyong on 2019/11/17.
 */
@Service
public class ScheduleRecoveryService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleRecoveryService.class);

    private static RecoveryStore recoveryStore;

    @Autowired
    private ScheduleLogRecoveryAction scheduleLogRecoveryAction;

    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl("recoveryThread"));

    @PostConstruct
    public void init() throws Exception {
        recoveryStore = new RecoveryStore();
        recoveryStore.start();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    List<RecoveryMessage> recoveryMessageList = recoveryStore.queryList(100);
                    for (RecoveryMessage recoveryMessage : recoveryMessageList) {
                        recoveryMessage(recoveryMessage);
                    }
                } catch (Exception e) {
                    logger.error("run error:", e);
                }
            }
        }, 20000L, 20000L, TimeUnit.MILLISECONDS);
    }

    public void doInsertRecoveryStore(RecoveryMessage recoveryMessage) throws UnsupportedEncodingException, RocksDBException {
        //先入库
        recoveryStore.put(recoveryMessage.getKey(), recoveryMessage);
        //恢复数据
        recoveryMessage(recoveryMessage);
    }

    public void recoveryMessage(RecoveryMessage recoveryMessage) {
        logger.info("recoveryMessage:" + JSON.toJSONString(recoveryMessage));
        boolean isSuccess = false;
        try {
            isSuccess = scheduleLogRecoveryAction.doAction(recoveryMessage);
        } catch (Exception e) {
            logger.error("doAction error: recoveryMessage:" + JSON.toJSONString(recoveryMessage), e);
        }
        if (isSuccess) {
            try {
                recoveryStore.delete(recoveryMessage.getKey());
            } catch (Exception e) {
                logger.error("delete error:", e);
            }
        } else {
            try {
                recoveryMessage.incrementRecoveryCount();
                recoveryStore.put(recoveryMessage.getKey(), recoveryMessage);
            } catch (Exception e) {
                logger.error("delete error:", e);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        if (recoveryStore != null) {
            recoveryStore.shutdown();
        }
        scheduledExecutorService.shutdown();
    }

}
