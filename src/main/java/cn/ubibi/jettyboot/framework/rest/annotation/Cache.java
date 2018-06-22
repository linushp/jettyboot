package cn.ubibi.jettyboot.framework.rest.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    long activeTime() default 5000;
    String cacheKey();
    int[] paramKey() default {};
}