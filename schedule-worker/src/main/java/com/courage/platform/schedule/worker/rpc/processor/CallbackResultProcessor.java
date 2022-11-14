package com.courage.platform.schedule.worker.rpc.processor;

import com.alibaba.fastjson.JSON;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyRequestProcessor;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingCommand;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingSerializable;
import com.courage.platform.schedule.rpc.protocol.CallbackCommand;
import com.courage.platform.schedule.worker.service.recovery.RecoveryCmdEnum;
import com.courage.platform.schedule.worker.service.recovery.RecoveryMessage;
import com.courage.platform.schedule.worker.service.recovery.ScheduleRecoveryService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/*
   回调任务处理结果处理器(client发送命令给server)
 */
public class CallbackResultProcessor implements PlatformNettyRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CallbackResultProcessor.class);

    @Autowired
    private ScheduleRecoveryService scheduleRecoveryService;

    @Override
    public PlatformRemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, PlatformRemotingCommand platformRemotingCommand) throws Exception {
        byte[] bytes = platformRemotingCommand.getBody();
        CallbackCommand callbackCommand = PlatformRemotingSerializable.decode(bytes, CallbackCommand.class);
        logger.info("callbackCommand:" + JSON.toJSONString(callbackCommand));

        //因为有可能数据没有准备好,所以先放入recoveryMessage使用
        RecoveryMessage recoveryMessage = new RecoveryMessage(callbackCommand.getJobLogId(), RecoveryCmdEnum.LOG_RECOVERY, JSON.toJSONString(callbackCommand));
        scheduleRecoveryService.doInsertRecoveryStore(recoveryMessage);

        PlatformRemotingCommand response = new PlatformRemotingCommand();
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}
