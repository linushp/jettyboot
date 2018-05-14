package cn.ubibi.jettyboot.framework.rest.annotation;

import java.lang.annotation.*;


/**
 * 全部是单例
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
}