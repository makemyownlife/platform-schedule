package com.courage.platform.schedule.worker.service.distribute;

import com.courage.platform.schedule.common.zookeeper.ZkClientx;
import com.courage.platform.schedule.common.zookeeper.ZookeeperPathUtils;
import com.courage.platform.schedule.core.util.IpUtil;
import com.courage.platform.schedule.core.util.StringUtils;
import com.courage.platform.schedule.rpc.ScheduleRpcServer;
import org.I0Itec.zkclient.IZkChildListener;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ZookeeperDistribute extends BaseDistribute implements DistributeService {

    private final static Logger logger = LoggerFactory.getLogger(ZookeeperDistribute.class);

    @Autowired
    private ScheduleRpcServer scheduleRpcServer;

    @Autowired
    private ZkClientx zkClientx;

    private String hostIp;

    //在leader目录下 创建临时节点
    private volatile String ownPath;

    //监控leader节点
    private Thread monitorThread;

    private volatile boolean running = false;

    @Override
    public void start() {
        this.hostIp = IpUtil.getIpPort(scheduleRpcServer.localListenPort());
        this.running = true;
        //核心思路是: 在zk /platform/schedule/servers 节点添加 127.0.0.1:12999
        //在/platform/schedule/servers/leader 节点下添加 若leader目录下存在节点 则监听节点 ，当前server状态是standby 若无节点，则做为leader ，作为任务分配的服务器
        long start = System.currentTimeMillis();
        logger.warn("开始启动zk任务分发器");
        //监听leader节点 若leader节点无leader 则申请为leader
        this.monitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        startZkService();
                    } catch (Throwable e) {
                        logger.error("start zk service:", e);
                    }
                    try {
                        Thread.sleep(60000);
                    } catch (Exception e) {
                    }
                }
            }
        });
        this.monitorThread.start();
        logger.warn("结束启动zk任务分发器,耗时:" + (System.currentTimeMillis() - start) + "ms");
    }

    private void startZkService() {
        //创建持久化节点
        preparePersistentNode();
        //创建临时节点
        tryEphemeralNode();
        //争取成为leader ，并启动任务
        leaderAction();
        //监听leader节点下子节点事件
        listenLeaderNodeChange();
    }

    private void leaderAction() {
        //争取成为leader
        boolean result = tryBecomeZkLeader();
        if (isLeader() != result) {
            setLeader(result);
            if (result) {
                startJobs();
            } else {
                destroyJobs();
            }
        }
    }

    private void preparePersistentNode() {
        //server永久节点
        if (!zkClientx.exists(ZookeeperPathUtils.SCHEDULE_SERVER_NODE)) {
            zkClientx.createPersistent(ZookeeperPathUtils.SCHEDULE_SERVER_NODE, true);
        }
        //leader永久节点
        if (!zkClientx.exists(ZookeeperPathUtils.SCHEDULE_LEADER_NODE)) {
            zkClientx.createPersistent(ZookeeperPathUtils.SCHEDULE_LEADER_NODE, true);
        }
    }

    private void tryEphemeralNode() {
        String hostPath = ZookeeperPathUtils.SCHEDULE_SERVER_NODE + ZookeeperPathUtils.ZOOKEEPER_SEPARATOR + IpUtil.getIpPort(scheduleRpcServer.localListenPort());
        if (!zkClientx.exists(hostPath)) {
            zkClientx.createEphemeral(hostPath, IpUtil.getIpPort(scheduleRpcServer.localListenPort()));
            logger.info("创建临时节点:" + hostPath);
        }
        if (StringUtils.isEmpty(ownPath)) {
            //创建leader节点下子节点
            this.ownPath = zkClientx.createEphemeralSequential(ZookeeperPathUtils.SCHEDULE_LEADER_NODE + ZookeeperPathUtils.ZOOKEEPER_SEPARATOR, hostIp);
            logger.info("ownPath:" + ownPath);
        }
    }

    private boolean tryBecomeZkLeader() {
        try {
            if (StringUtils.isEmpty(ownPath)) {
                logger.error("没有创建ownPath,请查看");
                return false;
            } else {
                //获取leader目录下 所有子节点
                List<String> children = zkClientx.getChildren(ZookeeperPathUtils.SCHEDULE_LEADER_NODE);
                //排序
                Collections.sort(children);
                if (CollectionUtils.isEmpty(children)) {
                    logger.error("当前leader节点无子节点....");
                    return false;
                }
                if (this.ownPath != null) {
                    String smallLeastNode = ZookeeperPathUtils.getLeaderChildPath(children.get(0));
                    if (StringUtils.equals(smallLeastNode, ownPath)) {
                        logger.warn("hostIp:" + hostIp + "成为leader");
                        return true;
                    }
                    //若当前值比最小值还小,则需要清空当前ownPath
                    if (ownPath.compareTo(smallLeastNode) < 0) {
                        logger.warn("hostIp:" + hostIp + " 若当前值比最小值还小,清空ownPath");
                        this.ownPath = null;
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("becomeLeader error:", e);
        }
        return false;
    }

    private void listenLeaderNodeChange() {
        //当没有成为leader时，监听子节点事件 当前没有考虑事件风暴的问题（服务器数量少的情况下 可以忽略）
        zkClientx.subscribeChildChanges(ZookeeperPathUtils.SCHEDULE_LEADER_NODE, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                try {
                    leaderAction();
                } catch (Throwable e) {
                    logger.error("handleChildChange parentPath:" + parentPath, e);
                }
            }
        });
    }

    @Override
    public void shutdown() {
        this.running = false;
        this.monitorThread = null;
    }

}
