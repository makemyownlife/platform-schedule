package com.courage.platform.schedule.worker.rpc.processor;

import com.alibaba.fastjson.JSON;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyRequestProcessor;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingCommand;
import com.courage.platform.schedule.rpc.protocol.RegisterCommand;
import com.courage.platform.schedule.worker.rpc.RpcChannelManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * 注册处理器
 * Created by zhangyong on 2019/11/7.
 */
public class RegisterProcessor implements PlatformNettyRequestProcessor {

    private final static Logger logger = LoggerFactory.getLogger(RegisterProcessor.class);

    @Autowired
    private RpcChannelManager rpcChannelManager;

    @Value("${auth:false}")
    private boolean auth;

    @Override
    public PlatformRemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, PlatformRemotingCommand platformRemotingCommand) throws Exception {
        byte[] bytes = platformRemotingCommand.getBody();
        RegisterCommand registerCommand = JSON.parseObject(bytes, RegisterCommand.class);

        String appName = registerCommand.getAppName();
        String clientId = registerCommand.getClientId();

        rpcChannelManager.createChannelSession(channelHandlerContext.channel(), appName, clientId);
        PlatformRemotingCommand responseCommand = new PlatformRemotingCommand();
        return responseCommand;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}
