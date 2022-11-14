package com.courage.platform.schedule.worker.service.recovery;

/**
 * 恢复数据
 * Created by zhangyong on 2019/11/14.
 */
public class RecoveryMessage {

    private Integer cmd;

    private String key;

    private Long createTime = System.currentTimeMillis();

    private String json;

    //恢复次数，默认恢复十次失败 则删除消息
    private int recoveryCount = 0;

    public RecoveryMessage(String key, Integer cmd, String json) {
        this.key = key;
        this.cmd = cmd;
        this.json = json;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getRecoveryCount() {
        return recoveryCount;
    }

    public void setRecoveryCount(int recoveryCount) {
        this.recoveryCount = recoveryCount;
    }

    public int incrementRecoveryCount() {
        return recoveryCount++;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

}