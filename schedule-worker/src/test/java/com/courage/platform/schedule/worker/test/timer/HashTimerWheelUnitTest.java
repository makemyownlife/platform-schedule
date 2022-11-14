package com.courage.platform.schedule.worker.test.timer;

import com.courage.platform.schedule.worker.service.timer.ScheduleHashedWheelTimer;
import com.courage.platform.schedule.worker.service.timer.ScheduleTimeout;
import com.courage.platform.schedule.worker.service.timer.ScheduleTimerTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyong on 2019/11/4.
 */
public class HashTimerWheelUnitTest {

    public static void main(String[] args) throws InterruptedException {
        ScheduleHashedWheelTimer scheduleHashedWheelTimer = new ScheduleHashedWheelTimer(1, TimeUnit.SECONDS, 3600);
        ScheduleTimeout scheduleTimeout1 = scheduleHashedWheelTimer.newTimeout(new ScheduleTimerTask() {
            @Override
            public void run(ScheduleTimeout timeout) throws Exception {
                System.out.println(12121);
            }
        }, 10, TimeUnit.SECONDS);

        ScheduleTimeout scheduleTimeout2 = scheduleHashedWheelTimer.newTimeout(new ScheduleTimerTask() {
            @Override
            public void run(ScheduleTimeout timeout) throws Exception {
                System.out.println(2222);
            }
        }, 5, TimeUnit.SECONDS);

        scheduleHashedWheelTimer.start();
        Thread.sleep(20000);
        //可以取消任务
        scheduleTimeout1.cancel();
    }

}
