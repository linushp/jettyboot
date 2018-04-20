package cn.ubibi.jettyboot.framework.rest.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {
    String value();
}