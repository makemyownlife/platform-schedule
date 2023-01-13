package com.courage.platform.schedule.rpc;

import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyClientConfig;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyRemotingClient;
import com.courage.platform.rpc.remoting.netty.codec.PlatformNettyRequestProcessor;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingCommand;
import com.courage.platform.rpc.remoting.netty.protocol.PlatformRemotingSerializable;
import com.courage.platform.schedule.rpc.protocol.BaseCommand;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务客户端
 * Created by zhangyong on 2018/10/3.
 */
public class ScheduleRpcClient implements ScheduleRpcService {

    private final static int POOL_CORE_SIZE = 5;

    private final static int POOL_MAX_SIZE = 5;

    private final static ThreadPoolExecutor scheduleClientThread = new ThreadPoolExecutor(POOL_CORE_SIZE, POOL_MAX_SIZE, 500, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100000), new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ScheduleClientThreadPool_" + this.threadIndex.incrementAndGet());
        }
    }, new ThreadPoolExecutor.CallerRunsPolicy());

    private volatile boolean inited = false;

    private PlatformNettyRemotingClient platformNettyRemotingClient;

    private PlatformNettyClientConfig platformNettyClientConfig;

    public ScheduleRpcClient() {
        this.platformNettyClientConfig = new PlatformNettyClientConfig();
        this.platformNettyRemotingClient = new PlatformNettyRemotingClient(platformNettyClientConfig);
    }

    public synchronized void start() {
        if (!inited) {
            this.platformNettyRemotingClient.start();
            inited = true;
        }
    }

    public void registerProcessor(int requestCmd, PlatformNettyRequestProcessor processor) {
        this.platformNettyRemotingClient.registerProcessor(requestCmd, processor, scheduleClientThread);
    }

    public PlatformRemotingCommand send(String remoteAddress, Integer requestCmd, BaseCommand scheduleCommand) throws Throwable {
        PlatformRemotingCommand requestCommand = new PlatformRemotingCommand();
        requestCommand.setRequestCmd(requestCmd);
        requestCommand.setTimestamp(System.currentTimeMillis());
        requestCommand.setBody(PlatformRemotingSerializable.encode(scheduleCommand));
        return this.platformNettyRemotingClient.invokeSync(remoteAddress, requestCommand, 3000L);
    }

    public void shutdown() {
        this.platformNettyRemotingClient.shutdown();
    }

}
