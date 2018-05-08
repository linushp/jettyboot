package cn.ubibi.jettyboot.framework.commons.ifs;

import cn.ubibi.jettyboot.framework.commons.BeanField;

import java.util.Map;

/**
 * 用户可以通过此方式自己扩展注解配置
 */
public interface MapToBeanFilter {

    /**
     * 从map中获取BeanField映射的值
     * @param beanField 类对象的字段类型
     * @param map 总的数据集合
     * @return 字段对应的数据
     * @throws Exception
     */
    Object getValue(BeanField beanField, Map<String, ?> map) throws Exception;

    /**
     * 数据类型转换
     * @param value1 字段对应的数据
     * @param beanField bean字段
     * @param map 总的数据集合
     * @return
     * @throws Exception
     */
    Object toBeanFieldType(Object value1, BeanField beanField, Map<String, ?> map) throws Exception;
}