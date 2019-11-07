package com.courage.platform.schedule.server.service;

import com.courage.platform.schedule.server.service.distribute.DatabaseDistribute;
import com.courage.platform.schedule.server.service.distribute.RaftDistribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 调度服务
 * Created by zhangyong on 2019/11/3.
 */
@Service
public class ScheduleDistributeService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleDistributeService.class);

    @Autowired
    private DatabaseDistribute databaseDistribute;

    @Autowired
    private RaftDistribute raftDistribute;

    @Value("${task_distribute_mode:0}")
    private int taskDistributeMode;

    @PostConstruct
    public void start() {
        //数据库模式 主从模式 任务跑在主机上
        if (taskDistributeMode == 0) {
            databaseDistribute.start();
        }
        //raft 协议，竞选leader,通过leader分配不同的任务到不同的机器上执行
        if (taskDistributeMode == 1) {
            raftDistribute.start();
        }
    }

    @PreDestroy
    public void shutdown() {
        //数据库模式 主从模式 任务跑在主机上
        if (taskDistributeMode == 0) {
            databaseDistribute.shutdown();
        }
        //raft 协议，竞选leader,通过leader分配不同的任务到不同的机器上执行
        if (taskDistributeMode == 1) {
            raftDistribute.shutdown();
        }
    }

}