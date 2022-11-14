package com.courage.platform.schedule.worker.rpc.processor;

import com.alibaba.fastjson.JSON;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyRequestProcessor;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingCommand;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingSerializable;
import com.courage.platform.schedule.rpc.protocol.ConsoleOnlineAppCommand;
import com.courage.platform.schedule.worker.rpc.RpcChannelManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 在线应用查询处理器
 * Created by zhangyong on 2019/11/15.
 */
public class ConsoleOnlineAppProcessor implements PlatformNettyRequestProcessor {

    private final static Logger logger = LoggerFactory.getLogger(ConsoleOnlineAppProcessor.class);

    @Autowired
    private RpcChannelManager rpcChannelManager;

    @Override
    public PlatformRemotingCommand processRequest(ChannelHandlerContext ctx, PlatformRemotingCommand request) throws Exception {
        byte[] bytes = request.getBody();
        ConsoleOnlineAppCommand consoleOnlineAppCommand = JSON.parseObject(bytes, ConsoleOnlineAppCommand.class);

        Map<String, Object> map = rpcChannelManager.searchOnlineApp(consoleOnlineAppCommand.getStart(), consoleOnlineAppCommand.getPageSize());
        PlatformRemotingCommand response = new PlatformRemotingCommand();
        response.setBody(PlatformRemotingSerializable.encode(map));
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}
