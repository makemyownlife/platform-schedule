package com.courage.platform.schedule.worker.rpc;

import com.alibaba.fastjson.annotation.JSONField;
import io.netty.channel.Channel;

/**
 * 链路对象
 * Created by zhangyong on 2019/11/7.
 */
public class RpcChannelSession {

    //应用名称
    private String appName;

    //客户端编号
    private String clientId;

    private String channelId;

    @JSONField(serialize = false)
    private Channel channel;

    private boolean auth;

    private long createTime = System.currentTimeMillis();

    private long updateTime = System.currentTimeMillis();

    public RpcChannelSession(String channelId, Channel channel, String appName, String clientId) {
        this.channelId = channelId;
        this.channel = channel;
        this.appName = appName;
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAppName() {
        return appName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

}
