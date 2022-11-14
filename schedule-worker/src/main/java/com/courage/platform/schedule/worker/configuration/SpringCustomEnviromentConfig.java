package com.courage.platform.schedule.worker.configuration;

import com.courage.platform.schedule.core.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 模仿springboot的各种环境配置信息
 * Created by zhangyong on 2019/11/1.
 */
public class SpringCustomEnviromentConfig extends PropertyPlaceholderConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(SpringCustomEnviromentConfig.class);

    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private Resource applicationFile;

    private Resource[] locations;

    @Override
    protected void loadProperties(Properties props) throws IOException {
        String jvmEnv = System.getProperty("spring.profiles.active");
        //获取当前应用env
        if (applicationFile != null) {
            InputStream inputStream = applicationFile.getInputStream();
            propertiesPersister.load(props, inputStream);
            String env = props.getProperty("spring.profiles.active");
            if (StringUtils.isEmpty(jvmEnv)) {
                jvmEnv = env;
            }
        }
        logger.info("应用启动env:" + jvmEnv + " 当前ip:" + IpUtil.getIp());
        if (locations != null) {
            logger.info("start loading properties file .........");
            for (Resource location : locations) {
                InputStream is = null;
                String fileName = location.getFilename();
                if (fileName.equals("application-" + jvmEnv + ".properties")) {
                    logger.info("加载环境配置文件:" + fileName);
                    try {
                        is = location.getInputStream();
                        propertiesPersister.load(props, is);
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setLocations(Resource[] locations) {
        this.locations = locations;
    }

    public void setApplicationFile(Resource applicationFile) {
        this.applicationFile = applicationFile;
    }

}
