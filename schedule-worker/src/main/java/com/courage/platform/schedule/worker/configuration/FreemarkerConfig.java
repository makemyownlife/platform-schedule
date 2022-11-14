package com.courage.platform.schedule.worker.configuration;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * freemarker模板配置
 */
@Service
public class FreemarkerConfig {

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerConfig.class);

    private Configuration configuration;

    public FreemarkerConfig() throws IOException {
        this.configuration = new Configuration(Configuration.VERSION_2_3_30);
        this.configuration.setDefaultEncoding("utf-8");
        this.configuration.setClassLoaderForTemplateLoading(ClassLoader.getSystemClassLoader(), "template");
        this.configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
        this.configuration.setNumberFormat("0.##########");
        this.configuration.setDateFormat("yyyy-MM-dd");
        this.configuration.setTimeFormat("HH:mm:ss");
        this.configuration.setClassicCompatible(true);
    }

    public String doTemplate(String templateName, Map<String, Object> param, boolean ignoreException) throws Exception {
        StringWriter writer = new StringWriter();
        try {
            Template template = this.configuration.getTemplate(templateName);
            template.process(param, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error("doTemplate error:", e);
            if (!ignoreException) {
                throw e;
            }
            return StringUtils.EMPTY;
        }
    }


    public String doRenderTemplate(String templateName, Map<String, Object> param) throws Exception {
        return doTemplate(templateName, param, true);
    }

    public static void main(String[] args) throws Exception {
        FreemarkerConfig freemarkerConfig = new FreemarkerConfig();
        String rtn = freemarkerConfig.doRenderTemplate("alarmMail.ftl", new HashMap<>());
        System.out.println(rtn);
    }

}
