package cn.ubibi.jettyboot.framework.rest.annotation;


import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value();
    String defaultValue() default "";

    //对于List或Set时有用到
    Class elementType() default String.class;
}