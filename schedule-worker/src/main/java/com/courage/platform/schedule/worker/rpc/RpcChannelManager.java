package com.courage.platform.schedule.worker.rpc;

import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * rpc 通道管理
 * Created by zhangyong on 2019/11/7.
 */
@Component
public class RpcChannelManager {

    private final static String CHANNEL_ID_KEY = "channelId";

    /*
      channelId ----> RpcChannelSession
     */
    private HashMap<String, RpcChannelSession> sessionHashMap = new HashMap<>(4048);

    /*
       appName ----> Set<ChannelId>
     */
    private HashMap<String, Set<String>> appNameChannelIdMapping = new HashMap<>(4048);

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public List<RpcChannelSession> getChannelSessionListByAppName(String appName) {
        readWriteLock.readLock().lock();
        try {
            Set<String> channelIdSet = appNameChannelIdMapping.get(appName);
            if (CollectionUtils.isEmpty(channelIdSet)) {
                return Collections.emptyList();
            }
            List<RpcChannelSession> dataList = new ArrayList<>(channelIdSet.size());
            for (String channelId : channelIdSet) {
                RpcChannelSession rpcChannelSession = sessionHashMap.get(channelId);
                if (rpcChannelSession != null) {
                    dataList.add(rpcChannelSession);
                }
            }
            return dataList;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public Map<String, Object> searchOnlineApp(int start, int pageSize) {
        List<RpcChannelSession> rpcChannelSessionList = new ArrayList<>();
        readWriteLock.readLock().lock();
        try {
            Iterator<Map.Entry<String, Set<String>>> iterator = appNameChannelIdMapping.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Set<String>> entry = (Map.Entry) iterator.next();
                Set<String> channelIdSet = entry.getValue();
                for (String channelId : channelIdSet) {
                    RpcChannelSession rpcChannelSession = sessionHashMap.get(channelId);
                    rpcChannelSessionList.add(rpcChannelSession);
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }

        //按照 update_time 来排序
        Collections.sort(rpcChannelSessionList, new Comparator<RpcChannelSession>() {
            @Override
            public int compare(RpcChannelSession o1, RpcChannelSession o2) {
                if (o1.getUpdateTime() > o2.getUpdateTime()) {
                    return 1;
                }
                if (o1.getUpdateTime() < o2.getUpdateTime()) {
                    return -1;
                }
                return 0;
            }
        });

        int size = rpcChannelSessionList.size();
        int maxIndex = size - 1 < 0 ? 0 : size;
        int startIndex = (start > maxIndex) ? maxIndex : start;
        int endIndex = start + pageSize > maxIndex ? maxIndex : start + pageSize;
        rpcChannelSessionList = rpcChannelSessionList.subList(startIndex, endIndex);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", rpcChannelSessionList.size());
        map.put("data", rpcChannelSessionList);
        return map;
    }

    public void bindChannelId(Channel channel) {
        Long channelId = ChannelUtils.getChannelId();
        ChannelUtils.putAttr(CHANNEL_ID_KEY, String.valueOf(channelId), channel);
    }

    public RpcChannelSession createChannelSession(Channel channel, String appName, String clientId) {
        readWriteLock.writeLock().lock();
        try {
            String channelId = (String) ChannelUtils.getAttr(CHANNEL_ID_KEY, channel);
            RpcChannelSession rpcChannelSession = sessionHashMap.get(channelId);
            if (rpcChannelSession == null) {
                rpcChannelSession = new RpcChannelSession(channelId, channel, appName, clientId);
                sessionHashMap.put(channelId, rpcChannelSession);
                Set<String> channelIdSet = appNameChannelIdMapping.get(appName);
                if (channelIdSet == null) {
                    channelIdSet = new HashSet<>();
                    appNameChannelIdMapping.put(appName, channelIdSet);
                }
                channelIdSet.add(channelId);
            } else {
                //修改心跳信息
                rpcChannelSession.setUpdateTime(System.currentTimeMillis());
            }
            return rpcChannelSession;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void closeChannelSession(Channel channel) {
        readWriteLock.writeLock().lock();
        try {
            String channelId = (String) ChannelUtils.getAttr(CHANNEL_ID_KEY, channel);
            RpcChannelSession rpcChannelSession = sessionHashMap.get(channelId);
            if (rpcChannelSession != null) {
                Set<String> channelIdSet = appNameChannelIdMapping.get(rpcChannelSession.getAppName());
                if (CollectionUtils.isNotEmpty(channelIdSet)) {
                    channelIdSet.remove(channelId);
                }
                sessionHashMap.remove(channelId);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
