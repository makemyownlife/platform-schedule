package com.courage.platform.schedule.admin.util;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;

public class FtlUtil {

    public static TemplateHashModel generateStaticModel(String packageName) {
        try {
            BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
            TemplateHashModel staticModels = wrapper.getStaticModels();
            TemplateHashModel fileStatics = (TemplateHashModel) staticModels.get(packageName);
            return fileStatics;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
