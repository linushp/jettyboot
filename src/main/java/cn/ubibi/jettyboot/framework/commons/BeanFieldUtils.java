package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.ifs.ObjectFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFieldUtils {

    /**
     * 成员变量字段
     */
    private static final Map<Class, List<BeanField>> beanFieldCacheMap = new ConcurrentHashMap<>();

    /**
     * 静态变量字段
     */
    private static final Map<Class, List<BeanField>> classStaticFieldCacheMap = new ConcurrentHashMap<>();


    //获取类字段（静态字段）
    public static List<BeanField> getClassStaticFields(Class clazz) {
        List<BeanField> beanFields = classStaticFieldCacheMap.get(clazz);
        if (beanFields == null) {
            beanFields = getClassOrBeanFields(clazz, new DefaultClassStaticFieldFilter());
            classStaticFieldCacheMap.put(clazz, beanFields);
        }
        return beanFields;
    }


    //获取对象的字段（非静态字段）
    public static List<BeanField> getBeanFields(Class clazz) {
        List<BeanField> beanFields = beanFieldCacheMap.get(clazz);
        if (beanFields == null) {
            beanFields = getClassOrBeanFields(clazz, new DefaultBeanFieldFilter());
            beanFieldCacheMap.put(clazz, beanFields);
        }
        return beanFields;
    }


    //过滤器
    private static class DefaultClassStaticFieldFilter implements ObjectFilter<Field> {
        public boolean isOK(Field field) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                return true;
            }
            return false;
        }
    }


    //过滤器
    private static class DefaultBeanFieldFilter implements ObjectFilter<Field> {
        public boolean isOK(Field field) {
            int modifiers = field.getModifiers();
            //过滤掉 static 和 final 的字符
            if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                return true;
            }
            return false;
        }
    }


    private static List<BeanField> getClassOrBeanFields(Class clazz, ObjectFilter<Field> filedFilter) {

        List<Class> classList = getSuperClass(clazz);
        classList.add(clazz);


        //1.得到所有的字段
        Map<String, Field> fieldMap = new HashMap<>();
        for (Class superClass : classList) {
            Field[] fields = superClass.getDeclaredFields();
            if (!CollectionUtils.isEmpty(fields)) {
                for (Field field : fields) {
                    fieldMap.put(field.getName(), field);
                }
            }
        }


        //2.过滤一些字段
        Collection<Field> fields = fieldMap.values();
        List<BeanField> result = new ArrayList<>();
        for (Field field : fields) {
            if (filedFilter.isOK(field)) {
                result.add(new BeanField(field));
            }
        }

        return result;
    }


    /**
     * 获取一个类的所有父类
     *
     * @param clazz 类
     * @return 所有父类
     */
    private static List<Class> getSuperClass(Class clazz) {
        List<Class> listSuperClass = new ArrayList<>();
        Class superclass = clazz.getSuperclass();
        while (superclass != null && !"java.lang.Object".equals(superclass.getName())) {
            listSuperClass.add(superclass);
            superclass = superclass.getSuperclass();
        }


        if (!listSuperClass.isEmpty()) {
            //反转
            Collections.reverse(listSuperClass);
        }
        return listSuperClass;
    }

}
