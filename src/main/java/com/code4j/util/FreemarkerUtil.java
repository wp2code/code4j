package com.code4j.util;

import com.code4j.enums.TemplateTypeEnum;
import com.code4j.pojo.BaseTemplateInfo;
import com.code4j.pojo.TemplateInfo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

/**
 * @author liu_wp
 * @date 2020/11/19
 * @see
 */
public class FreemarkerUtil {


    /**
     * @param projectPath
     * @param baseTemplateInfo
     * @param dataMap
     * @return
     */
    public static String generateCodeByTemplate(String projectPath, BaseTemplateInfo baseTemplateInfo, Map<String, Object> dataMap) {
        if (baseTemplateInfo != null && dataMap != null && !dataMap.isEmpty()) {
            Writer out = null;
            try {
                Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
                TemplateInfo templateInfo = baseTemplateInfo.getTemplateInfo();
//                File file = new File(templateInfo.getTemplatePath());
                configuration.setClassForTemplateLoading(FreemarkerUtil.class, "/templates");
                TemplateTypeEnum templateTypeEnum = TemplateTypeEnum.getTemplateTypeEnum(templateInfo.getTemplateId());
                String generatePojoFolder = baseTemplateInfo.getGeneratePojoFolder(projectPath);
                File folder = new File(generatePojoFolder);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File outFile = new File(generatePojoFolder + "\\" + baseTemplateInfo.getPojoName() + templateTypeEnum.getFileType());
                if (outFile.exists()) {
                    outFile.delete();
                }
                Template template = configuration.getTemplate(templateTypeEnum.getTemplateName());
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
                template.process(dataMap, out);
                out.flush();
                return outFile.getAbsolutePath();
            } catch (IOException | TemplateException e) {
                return e.getMessage();
            } finally {
                try {
                    if (null != out) {
                        out.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return null;
    }
}
