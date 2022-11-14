package com.courage.platform.schedule.admin.service;

import com.courage.platform.schedule.admin.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 登录服务
 * Created by zhangyong on 2019/11/21.
 */
@Service
public class LoginService {

    private final static Logger logger = LoggerFactory.getLogger(LoginService.class);
    
    public String createToken(String username) {
        String md5 = Md5Util.getMd5Code(username);
        return md5;
    }

}
