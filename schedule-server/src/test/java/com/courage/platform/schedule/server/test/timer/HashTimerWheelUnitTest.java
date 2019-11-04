package com.courage.platform.schedule.server.test.timer;

import com.courage.platform.schedule.server.service.timer.ScheduleHashedWheelTimer;
import com.courage.platform.schedule.server.service.timer.ScheduleTimeout;
import com.courage.platform.schedule.server.service.timer.ScheduleTimerTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyong on 2019/11/4.
 */
public class HashTimerWheelUnitTest {

    public static void main(String[] args) {
        ScheduleHashedWheelTimer scheduleHashedWheelTimer = new ScheduleHashedWheelTimer(3600, TimeUnit.SECONDS, 1);
        scheduleHashedWheelTimer.newTimeout(new ScheduleTimerTask() {
            @Override
            public void run(ScheduleTimeout timeout) throws Exception {
                System.out.println(12121);
            }
        }, 10, TimeUnit.SECONDS);


        scheduleHashedWheelTimer.newTimeout(new ScheduleTimerTask() {
            @Override
            public void run(ScheduleTimeout timeout) throws Exception {
                System.out.println(2222);
            }
        }, 5, TimeUnit.SECONDS);

        scheduleHashedWheelTimer.start();
    }

}