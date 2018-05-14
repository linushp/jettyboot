package cn.ubibi.jettyboot.framework.rest.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String[] value();

    boolean singleton() default true;
}