package com.courage.platform.schedule.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 任务调度主函数
 * Created by zhangyong on 2019/10/1
 */
public class MainApplication {

    private final static Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        logger.info("开始启动任务调度服务");
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-app.xml");
        logger.info("结束启动任务调度服务,耗时:" + (System.currentTimeMillis() - start) + "ms");
    }
    
}
