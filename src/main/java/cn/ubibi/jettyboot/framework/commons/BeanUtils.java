package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.ifs.BeanToMapFilter;
import cn.ubibi.jettyboot.framework.commons.ifs.MapToBeanFilter;
import cn.ubibi.jettyboot.framework.commons.impl.DefaultBeanToMapFilter;
import cn.ubibi.jettyboot.framework.commons.impl.DefaultMapToBeanFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BeanUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtils.class);

    public static List<Map<String, Object>> beanListToMapList(List<Object> beanList) throws IllegalAccessException {
        return beanListToMapList(beanList, false);
    }


    public static List<Map<String, Object>> beanListToMapList(List<Object> beanList, boolean isUnderlineKey) throws IllegalAccessException {
        return beanListToMapList(beanList, new DefaultBeanToMapFilter(isUnderlineKey));
    }


    public static List<Map<String, Object>> beanListToMapList(List<Object> beanList, BeanToMapFilter beanToMapFilter) throws IllegalAccessException {

        if (CollectionUtils.isEmpty(beanList)) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object object : beanList) {
            Map<String, Object> map = beanToMap(object, beanToMapFilter);
            if (map != null) {
                result.add(map);
            }
        }

        return result;
    }

    public static Map<String, Object> beanToMap(Object bean) throws IllegalAccessException {
        return beanToMap(bean, false);
    }


    public static Map<String, Object> beanToMap(Object bean, boolean isUnderlineKey) throws IllegalAccessException {
        return beanToMap(bean, new DefaultBeanToMapFilter(isUnderlineKey));
    }

    /**
     * 将Bean转化为map
     *
     * @param bean            对象
     * @param beanToMapFilter 是否自动将驼峰形式的key自动转化为下划线形式
     * @return map对象
     * @throws IllegalAccessException
     */
    public static Map<String, Object> beanToMap(Object bean, BeanToMapFilter beanToMapFilter) throws IllegalAccessException {
        if (bean == null) {
            return null;
        }

        List<BeanField> beanFields = BeanFieldUtils.getBeanFields(bean.getClass());
        Map<String, Object> map = new HashMap<>(beanFields.size());

        for (BeanField beanField : beanFields) {
            if (beanToMapFilter.isInclude(beanField)) {

                String mapKey = beanToMapFilter.getMapKey(beanField);

                Object value = beanField.getBeanValue(bean);

                value = beanToMapFilter.toMapValueType(value, beanField);

                map.put(mapKey, value);
            }
        }

        return map;
    }


    /**
     * map to bean list
     *
     * @param clazz  the type of target bean
     * @param values from values
     * @param <T>    type
     * @return the target bean list
     * @throws Exception 异常
     */
    public static <T> List<T> mapListToBeanList(Class<T> clazz, List<Map<String, ?>> values) throws Exception {
        MapToBeanFilter mapToBeanFilter = new DefaultMapToBeanFilter();
        return mapListToBeanList(clazz, values, mapToBeanFilter);
    }


    /**
     * map to bean list
     *
     * @param clazz  the type of target bean
     * @param values from values
     * @param <T>    type
     * @return the target bean list
     * @throws Exception 异常
     */
    public static <T> List<T> mapListToBeanList(Class<T> clazz, List<Map<String, ?>> values, MapToBeanFilter mapToBeanFilter) throws Exception {

        if (CollectionUtils.isEmpty(values)) {
            return new ArrayList<>();
        }


        List<BeanField> beanFields = BeanFieldUtils.getBeanFields(clazz);
        List<T> result = new ArrayList<>();

        for (Map<String, ?> m : values) {
            //通过反射创建一个其他类的对象
            T bean = BeanUtils.mapToBean(clazz, m, beanFields, mapToBeanFilter);
            result.add(bean);
        }

        return result;
    }


    public static <T> T mapToBean(Class<? extends T> clazz, Map<String, ?> map) throws Exception {
        MapToBeanFilter mapToBeanFilter = new DefaultMapToBeanFilter();
        return mapToBean(clazz, map, mapToBeanFilter);
    }


    public static <T> T mapToBean(Class<? extends T> clazz, Map<String, ?> map, MapToBeanFilter mapToBeanFilter) throws Exception {
        if (clazz == null || map == null) {
            return null;
        }
        List<BeanField> beanFields = BeanFieldUtils.getBeanFields(clazz);
        return mapToBean(clazz, map, beanFields, mapToBeanFilter);
    }


    private static <T> T mapToBean(Class<? extends T> clazz, Map<String, ?> map, List<BeanField> beanFields, MapToBeanFilter mapToBeanFilter) throws Exception {

        if (clazz == null || map == null || beanFields == null) {
            return null;
        }

        T bean = clazz.newInstance();
        if (CollectionUtils.isEmpty(beanFields)) {
            return bean;
        }


        for (BeanField beanField : beanFields) {
            //1.得到数据
            Object value1 = mapToBeanFilter.getValue(beanField, map);
            //2.类型转换
            value1 = mapToBeanFilter.toBeanFieldType(value1, beanField, map);
            if (value1 != null) {
                beanField.setBeanValue(bean, value1);
            }
        }

        return bean;
    }


    /**
     * 浅复制
     *
     * @param targetObject 目标对象
     * @param fromObject   源对象
     * @param <T>          类型
     */
    public static <T> void copyField(T targetObject, T fromObject) {
        if (fromObject == null) {
            return;
        }

        try {
            List<BeanField> fields = BeanFieldUtils.getBeanFields(fromObject.getClass());
            if (!CollectionUtils.isEmpty(fields)) {
                for (BeanField field : fields) {
                    Object value = field.getBeanValue(fromObject);
                    field.setBeanValue(targetObject, value);
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("", e);
        }
    }
}
