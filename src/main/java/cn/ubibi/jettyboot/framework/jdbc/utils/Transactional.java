package cn.ubibi.jettyboot.framework.jdbc.utils;


import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    String connectionFactoryName() default FrameworkConfig.DEFAULT_CONNECTION_FACTORY_NAME;
}