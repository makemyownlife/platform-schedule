package com.courage.platform.schedule.worker.service;

import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/*
  报警服务
 */
@Service
public class ScheduleAlarmService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleAlarmService.class);

    public static boolean sendTextMail(String strMail, String strTitle, String strText) throws Exception {
        boolean bret = false;
        SimpleEmail mail = new SimpleEmail();
        // 设置邮箱服务器信息
        mail.setSslSmtpPort("25");
        mail.setHostName("smtp.163.com");
        // 设置密码验证器
        mail.setAuthentication("zhangyongxiongwe@163.com", "UMYGZIYIFXNNJGOL");
        // 设置邮件发送者
        mail.setFrom("zhangyongxiongwe@163.com");
        // 设置邮件接收者
        mail.addTo(strMail);
        // 设置邮件编码
        mail.setCharset("UTF-8");
        // 设置邮件主题
        mail.setSubject(strTitle);
        // 设置邮件内容
        mail.setMsg(strText);
        // 设置邮件发送时间
        mail.setSentDate(new Date());
        // 发送邮件
        mail.send();
        return bret;
    }

    public static void main(String[] args) throws Exception {
        sendTextMail("zhangyong7120180@163.com" , "hello" , "mylife");
    }

}
