package com.courage.platform.schedule.worker.service;

import com.courage.platform.schedule.worker.service.distribute.RaftDistribute;
import com.courage.platform.schedule.worker.service.distribute.ZookeeperDistribute;
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

    @Autowired
    private ZookeeperDistribute zookeeperDistribute;

    @Autowired
    private RaftDistribute raftDistribute;

    @Value("${task_distribute_mode:1}")
    private int taskDistributeMode;

    @PostConstruct
    public void start() {
        //数据库模式 主从模式 任务跑在主机上
        if (taskDistributeMode == 1) {
            zookeeperDistribute.start();
        }
        //raft 协议，竞选leader,通过leader分配不同的任务到不同的机器上执行
        if (taskDistributeMode == 2) {
            raftDistribute.start();
        }
    }

    @PreDestroy
    public void shutdown() {
        //数据库模式 主从模式 任务跑在主机上
        if (taskDistributeMode == 1) {
            zookeeperDistribute.shutdown();
        }
        //raft 协议，竞选leader,通过leader分配不同的任务到不同的机器上执行
        if (taskDistributeMode == 2) {
            raftDistribute.shutdown();
        }
    }

}
