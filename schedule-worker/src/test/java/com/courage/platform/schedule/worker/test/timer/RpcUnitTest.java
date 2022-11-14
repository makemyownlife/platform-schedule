package com.courage.platform.schedule.worker.test.timer;

import com.alibaba.fastjson.JSON;
import com.courage.platform.rpc.remoting.exception.PlatformRemotingConnectException;
import com.courage.platform.rpc.remoting.exception.PlatformRemotingSendRequestException;
import com.courage.platform.rpc.remoting.exception.PlatformRemotingTimeoutException;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyClientConfig;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyRemotingClient;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyRequestProcessor;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingCommand;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingSerializable;
import com.courage.platform.schedule.rpc.protocol.CommandEnum;
import com.courage.platform.schedule.rpc.protocol.ConsoleOnlineAppCommand;
import io.netty.channel.ChannelHandlerContext;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * rpc 单元测试
 * Created by zhangyong on 2019/11/7.
 */
public class RpcUnitTest {

    public static void main(String[] args) throws InterruptedException, PlatformRemotingTimeoutException, PlatformRemotingConnectException, PlatformRemotingSendRequestException {
        PlatformNettyRemotingClient platformNettyRemotingClient = new PlatformNettyRemotingClient(new PlatformNettyClientConfig());
        platformNettyRemotingClient.registerProcessor(CommandEnum.TRIGGER_SCHEDULE_TASK_CMD, new PlatformNettyRequestProcessor() {
            @Override
            public PlatformRemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, PlatformRemotingCommand platformRemotingCommand) throws Exception {
                System.out.println(platformRemotingCommand);
                return null;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, newFixedThreadPool(2));
        platformNettyRemotingClient.start();
        PlatformRemotingCommand requestCommand = new PlatformRemotingCommand();
        requestCommand.putHeadParam("appName", "com.courage.test");
        requestCommand.putHeadParam("clientId", "xxxxxxxxx");
        requestCommand.setRequestCmd(CommandEnum.REGISTER_CMD);
        requestCommand.setTimestamp(System.currentTimeMillis());
        requestCommand.setBody(PlatformRemotingSerializable.encode("mylife"));
        //   PlatformRemotingCommand response = platformNettyRemotingClient.invokeSync("localhost:12999", requestCommand, 3000L);

        PlatformRemotingCommand platformRemotingCommand = new PlatformRemotingCommand();
        platformRemotingCommand.setRequestCmd(CommandEnum.CONSOLE_ONLINE_APP_CMD);
        ConsoleOnlineAppCommand requestCommand2 = new ConsoleOnlineAppCommand();
        requestCommand2.setStart(0);
        requestCommand2.setPageSize(10);
        platformRemotingCommand.setBody(JSON.toJSONBytes(requestCommand2));
        PlatformRemotingCommand response2 = platformNettyRemotingClient.invokeSync("localhost:12999", platformRemotingCommand, 3000L);

        Thread.sleep(100000);
    }

}
