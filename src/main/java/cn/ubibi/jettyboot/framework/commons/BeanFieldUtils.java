package cn.ubibi.jettyboot.framework.commons;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class BeanFieldUtils {

    private static final Map<Class,List<BeanField>> beanFieldCacheMap = new HashMap<>();


    public static List<BeanField> getBeanFields(Class clazz) {
        List<BeanField> beanFields = beanFieldCacheMap.get(clazz);
        if (beanFields == null){
            beanFields = getBeanFields1(clazz);
            beanFieldCacheMap.put(clazz,beanFields);
        }
        return beanFields;
    }




    private static List<BeanField> getBeanFields1(Class clazz) {

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


        //2.过滤掉static和final的字段
        Collection<Field> fields = fieldMap.values();
        List<BeanField> result = new ArrayList<>();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)){
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


        if (!listSuperClass.isEmpty()){
            //反转
            Collections.reverse(listSuperClass);
        }
        return listSuperClass;
    }

}
