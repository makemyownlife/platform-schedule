package com.courage.platform.schedule.worker.rpc.processor;

import com.alibaba.fastjson.JSON;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyRequestProcessor;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingCommand;
import com.courage.platform.schedule.rpc.protocol.ConsoleTriggerCommand;
import com.courage.platform.schedule.worker.service.ScheduleTriggerService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 其他方调用server 来实现自动执行
 * Created by zhangyong on 2019/11/11.
 */
public class ConsoleTriggerProcessor implements PlatformNettyRequestProcessor {

    private final static Logger logger = LoggerFactory.getLogger(ConsoleTriggerProcessor.class);

    @Autowired
    private ScheduleTriggerService scheduleTriggerService;

    @Override
    public PlatformRemotingCommand processRequest(ChannelHandlerContext ctx, PlatformRemotingCommand request) throws Exception {
        byte[] bytes = request.getBody();
        ConsoleTriggerCommand consoleTriggerCommand = JSON.parseObject(bytes, ConsoleTriggerCommand.class);
        scheduleTriggerService.doRpcTrigger(consoleTriggerCommand.getJobId());
        PlatformRemotingCommand response = new PlatformRemotingCommand();
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}
