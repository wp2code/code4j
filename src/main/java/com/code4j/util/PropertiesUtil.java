package com.code4j.util;

import com.code4j.annotation.IgnoreReflection;
import com.code4j.annotation.PropertyKeyIndexId;
import com.code4j.config.Code4jConstants;
import com.code4j.pojo.JdbcSourceInfo;
import com.code4j.pojo.ProjectCodeConfigInfo;
import com.code4j.pojo.PropertyInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liu_wp
 * @date 2020/11/9
 * @see
 */
public class PropertiesUtil {

    /**
     * @param fileName
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getPropertyObject(String fileName, Class<T> tClass) {
        try {
            Properties properties = new Properties();
            properties.load(getPropertyStream(fileName));
            T t = tClass.newInstance();
            Field[] declaredFields = tClass.getDeclaredFields();
            for (final Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                String name = declaredField.getName();
                Object value = properties.get(name);
                if (value != null) {
                    declaredField.set(t, value);
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param fileName
     * @param keyPrefix
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> getPropertyValues(String fileName, String keyPrefix, Class<T> tClass) {
        try (InputStream inputStream = PropertiesUtil.getPropertyStream(fileName)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            Set<String> stringSet = properties.stringPropertyNames();
            Map<String, List<PropertyInfo>> map = new HashMap<>();
            for (final String key : stringSet) {
                String value = properties.getProperty(key);
                if (key.startsWith(keyPrefix)) {
                    String[] split = key.split("[.]");
                    String k1 = split[0];
                    String k2 = split[1];
                    String k1v = k1.substring(keyPrefix.length() + 1, keyPrefix.length() + 2);
                    List<PropertyInfo> listMap = null;
                    if (map.get(k1v) == null) {
                        listMap = new ArrayList<>();
                    } else {
                        listMap = map.get(k1v);
                    }
                    PropertyInfo newMap = new PropertyInfo(k2, value);
                    if (CollectionUtils.isEmpty(listMap) || !listMap.stream().anyMatch(v -> v.equals(newMap))) {
                        listMap.add(newMap);
                    }
                    map.put(k1v, listMap);
                }
            }
            if (!map.isEmpty()) {
                List<T> list = new ArrayList<>();
                for (Map.Entry<String, List<PropertyInfo>> pMap : map.entrySet()) {
                    Map<String, String> vMap = pMap.getValue().stream().collect(Collectors.toMap(PropertyInfo::getKey, PropertyInfo::getValue));
                    T newInstance = tClass.newInstance();
                    Field[] declaredFields = tClass.getDeclaredFields();
                    boolean isAdd = false;
                    for (final Field declaredField : declaredFields) {
                        declaredField.setAccessible(true);
                        String name = declaredField.getName();
                        IgnoreReflection annotation = declaredField.getAnnotation(IgnoreReflection.class);
                        if (annotation != null) {
                            continue;
                        }
                        String value = vMap.get(name);
                        if (StringUtils.isNotBlank(value)) {
                            isAdd = true;
                            String type = declaredField.getGenericType().getTypeName();
                            if (type.equals(Integer.class.getTypeName())) {
                                declaredField.set(newInstance, Integer.valueOf(value));
                            } else if (type.equals(Double.class.getTypeName())) {
                                declaredField.setDouble(newInstance, Double.valueOf(value));
                            } else {
                                declaredField.set(newInstance, vMap.get(name));
                            }
                        }
                    }
                    if (isAdd && newInstance != null) {
                        list.add(newInstance);
                    }
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * @param fileName
     * @param object
     * @return
     */
    public static boolean setPropertyValue(String fileName, Object object) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(getPropertyPath(fileName));) {
            Properties properties = new Properties();
            properties.load(getPropertyStream(fileName));
            Class<?> aClass = object.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (final Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object value = declaredField.get(object);
                if (value != null) {
                    properties.setProperty(declaredField.getName(), value.toString());
                }
            }
            properties.store(fileOutputStream, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取数据库配置信息
     *
     * @return
     */
    public static List<JdbcSourceInfo> getJdbcPropertyValues() {
        List<JdbcSourceInfo> proList = getPropertyValues(Code4jConstants.DEFAULT_DB_CONFIG_FILE_NAME, Code4jConstants.DEFAULT_DB_CONFIG_KEY_PREFIX, JdbcSourceInfo.class);
        if (CollectionUtils.isNotEmpty(proList) && proList.size() > 1) {
            proList.stream().filter(v -> v != null).collect(Collectors.toList()).sort((v1, v2) -> v1.getIndex().compareTo(v2.getIndex()));
        }
        return proList;
    }

    /**
     * 获取项目配置信息
     *
     * @return
     */
    public static List<ProjectCodeConfigInfo> getProjectConfigPropertyValues() {
        List<ProjectCodeConfigInfo> proList = getPropertyValues(Code4jConstants.DEFAULT_PROJECT_CONFIG_FILE_NAME, Code4jConstants.DEFAULT_DB_PROJECT_CONFIG_KEY_PREFIX, ProjectCodeConfigInfo.class);
        if (CollectionUtils.isNotEmpty(proList) && proList.size() > 1) {
            proList.stream().filter(v -> v != null).collect(Collectors.toList()).sort((v1, v2) -> v1.getIndex().compareTo(v2.getIndex()));
        }
        return proList;
    }

    /**
     * @param projectCodeConfigInfo
     * @param isUpdate
     * @return
     */
    public static boolean setProjectConfigPropertyValues(ProjectCodeConfigInfo projectCodeConfigInfo, boolean isUpdate) {
        if (projectCodeConfigInfo != null) {
            List<ProjectCodeConfigInfo> projectCodeConfigInfos = getProjectConfigPropertyValues();
            if (!CollectionUtils.isEmpty(projectCodeConfigInfos)) {
                if (isUpdate) {
                    for (ProjectCodeConfigInfo codeConfigInfo : projectCodeConfigInfos) {
                        if (projectCodeConfigInfo.getIndex().equals(codeConfigInfo.getIndex())) {
                            codeConfigInfo.setServiceApiPath(projectCodeConfigInfo.getServiceApiPath());
                            codeConfigInfo.setServiceApiPackageName(projectCodeConfigInfo.getServiceApiPackageName());
                            codeConfigInfo.setMapperPath(projectCodeConfigInfo.getMapperPath());
                            codeConfigInfo.setMapperPackageName(projectCodeConfigInfo.getMapperPackageName());
                            codeConfigInfo.setXmlPath(projectCodeConfigInfo.getXmlPath());
                            codeConfigInfo.setXmlPackageName(projectCodeConfigInfo.getXmlPackageName());
                            codeConfigInfo.setVoPath(projectCodeConfigInfo.getVoPath());
                            codeConfigInfo.setVoPackageName(projectCodeConfigInfo.getVoPackageName());
                            codeConfigInfo.setDoPath(projectCodeConfigInfo.getDoPath());
                            codeConfigInfo.setDoPackageName(projectCodeConfigInfo.getDoPackageName());
                            codeConfigInfo.setProjectName(projectCodeConfigInfo.getProjectName());
                            codeConfigInfo.setVoSuperClass(projectCodeConfigInfo.getVoSuperClass());
                            codeConfigInfo.setDoSuperClass(projectCodeConfigInfo.getDoSuperClass());
                            codeConfigInfo.setMapperSuperClass(projectCodeConfigInfo.getMapperSuperClass());
                            codeConfigInfo.setServiceSuperClass(projectCodeConfigInfo.getServiceSuperClass());
                            break;
                        }
                    }
                } else {
                    projectCodeConfigInfos.add(projectCodeConfigInfo);
                }
                return setPropertyValues(Code4jConstants.DEFAULT_PROJECT_CONFIG_FILE_NAME, Code4jConstants.DEFAULT_DB_PROJECT_CONFIG_KEY_PREFIX, projectCodeConfigInfos);
            }
            return setPropertyValues(Code4jConstants.DEFAULT_PROJECT_CONFIG_FILE_NAME, Code4jConstants.DEFAULT_DB_PROJECT_CONFIG_KEY_PREFIX, Arrays.asList(projectCodeConfigInfo));
        }
        return false;
    }

    /**
     * @param projectCodeConfigInfo
     * @return
     */
    public static boolean deleteProjectConfigProperty(ProjectCodeConfigInfo projectCodeConfigInfo) {
        return removeProperty(Code4jConstants.DEFAULT_PROJECT_CONFIG_FILE_NAME, Code4jConstants.DEFAULT_DB_PROJECT_CONFIG_KEY_PREFIX, projectCodeConfigInfo, projectCodeConfigInfo.getIndex());
    }
    /**
     * @param jdbcSourceInfo
     * @return
     */
    public static boolean deleteJdbcProperty(JdbcSourceInfo jdbcSourceInfo) {
        return removeProperty(Code4jConstants.DEFAULT_DB_CONFIG_FILE_NAME, Code4jConstants.DEFAULT_DB_CONFIG_KEY_PREFIX, jdbcSourceInfo, jdbcSourceInfo.getIndex());
    }


    /**
     * @param jdbcSourceInfo
     * @return
     */
    public static boolean setJdbcPropertyValues(JdbcSourceInfo jdbcSourceInfo) {
        if (jdbcSourceInfo != null) {
            List<JdbcSourceInfo> proList = getJdbcPropertyValues();
            if (CollectionUtils.isNotEmpty(proList)) {
                boolean isUpdate = false;
                for (JdbcSourceInfo sourceInfo : proList) {
                    if (isUpdate = sourceInfo.getIndex().equals(jdbcSourceInfo.getIndex())) {
                        sourceInfo.setConnectName(jdbcSourceInfo.getConnectName());
                        sourceInfo.setConnectHost(jdbcSourceInfo.getConnectHost());
                        sourceInfo.setConnectPort(jdbcSourceInfo.getConnectPort());
                        sourceInfo.setUserName(jdbcSourceInfo.getUserName());
                        sourceInfo.setPassword(jdbcSourceInfo.getPassword());
                        sourceInfo.setCreator(jdbcSourceInfo.getCreator());
                        sourceInfo.setIndex(jdbcSourceInfo.getIndex());
                        sourceInfo.setDataSourceTypeEnum(jdbcSourceInfo.getDataSourceTypeEnum());
                        break;
                    }
                }
                if (!isUpdate) {
                    proList.add(jdbcSourceInfo);
                }
                return setPropertyValues(Code4jConstants.DEFAULT_DB_CONFIG_FILE_NAME, Code4jConstants.DEFAULT_DB_CONFIG_KEY_PREFIX, proList);
            }
            return setPropertyValues(Code4jConstants.DEFAULT_DB_CONFIG_FILE_NAME, Code4jConstants.DEFAULT_DB_CONFIG_KEY_PREFIX, Arrays.asList(jdbcSourceInfo));
        }
        return false;
    }

    /**
     * @param fileName
     * @param keyPrefix
     * @param objects
     * @param <T>
     * @return
     */
    public static <T> boolean setPropertyValues(String fileName, String keyPrefix, List<T> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            return false;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(getPropertyPath(fileName));
             InputStream inputStream = getPropertyStream(fileName);) {
            Properties properties = new Properties();
            properties.load(inputStream);
            for (int i = 0; i < objects.size(); i++) {
                T object = objects.get(i);
                Class<?> aClass = object.getClass();
                PropertyKeyIndexId propertyKeyIndexId = aClass.getAnnotation(PropertyKeyIndexId.class);
                String indexFiledName = propertyKeyIndexId.fieldName();
                Field indexField = aClass.getDeclaredField(indexFiledName);
                indexField.setAccessible(true);
                String index = i + "";
                if (indexField != null && indexField.get(object) != null) {
                    index = indexField.get(object).toString();
                }
                Field[] declaredFields = aClass.getDeclaredFields();
                for (final Field declaredField : declaredFields) {
                    IgnoreReflection annotation = declaredField.getAnnotation(IgnoreReflection.class);
                    if (annotation != null) {
                        continue;
                    }
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(object);
                    String key = keyPrefix + "[" + index + "]." + declaredField.getName();
                    System.out.println("编辑key:" + key);
                    properties.setProperty(key, value != null ? value.toString() : "");
                }
            }
            properties.store(fileOutputStream, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param fileName
     * @param keyPrefix
     * @param object
     * @param index
     * @param <T>
     * @return
     */
    public static <T> boolean removeProperty(String fileName, String keyPrefix, T object, Integer index) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            Properties properties = new Properties();
            properties.load(inputStream = getPropertyStream(fileName));
            Class<?> aClass = object.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (final Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                IgnoreReflection annotation = declaredField.getAnnotation(IgnoreReflection.class);
                if (annotation != null) {
                    continue;
                }
                String key = keyPrefix + "[" + index + "]." + declaredField.getName();
                if (properties.containsKey(key)) {
                    System.out.println("删除文件key：" + key);
                    properties.remove(key);
                }
            }
            fileOutputStream = new FileOutputStream(getPropertyPath(fileName));
            properties.store(fileOutputStream, "");
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
            }

        }
    }

    /**
     * @param fileName
     * @return
     */
    public static InputStream getPropertyStream(String fileName) {
        String propertyPath = getPropertyPath(fileName);
        try {
            return new FileInputStream(propertyPath);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        return null;
    }

    /**
     * @param fileName
     * @return
     */
    public static InputStream getPropertySysStream(String fileName) {
        try {
            InputStream resourceAsStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
            return resourceAsStream;
        } catch (Exception e) {

        }
        return null;
    }

    public static String getPropertyPath(String fileName) {
        String path = Code4jConstants.PROJECT_ROOT_PATH + "\\" + fileName;
        File file = new File(path);
        if (!file.exists()) {
            file = new File(path = Code4jConstants.SYS_TEMP_PATH + "\\" + fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        System.out.println(path);
        return path;
    }


    public static void main(String[] args) throws Exception {
//        List<JdbcSourceInfo> list = getPropertyValues("log4j2.properties", "dataSource", JdbcSourceInfo.class);
//        System.out.println(list);
//        JdbcSourceInfo jdbcSourceInfo = new JdbcSourceInfo();
//        jdbcSourceInfo.setConnectHost("122222");
//        jdbcSourceInfo.setConnectName("测试121212rqewrqw");
//        jdbcSourceInfo.setPassword("dffd20000");
//        jdbcSourceInfo.setUserName("wodfd111");
//        jdbcSourceInfo.setConnectPort(33891);
////        list.add(jdbcSourceInfo);
//        PropertiesUtil.setJdbcPropertyValues(jdbcSourceInfo);
//        PropertiesUtil.deleteJdbcProperty(jdbcSourceInfo);
//        System.out.println(JSONUtil.Object2JSON(list));
        System.out.println(System.getProperty("user.home"));
    }
}
