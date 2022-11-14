package com.courage.platform.schedule.worker.rpc;

import com.courage.platform.rpc.remoting.PlatformChannelEventListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * rpc channel 变化监听器
 * Created by zhangyong on 2019/11/7.
 */
public class RpcChannelListener implements PlatformChannelEventListener {

    private static final Logger logger = LoggerFactory.getLogger(RpcChannelListener.class);

    @Autowired
    private RpcChannelManager rpcChannelManager;

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        logger.info("connect远程链接:" + remoteAddr);
        rpcChannelManager.bindChannelId(channel);
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        logger.info("close远程链接:" + remoteAddr);
        rpcChannelManager.closeChannelSession(channel);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {

    }

}
