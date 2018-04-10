package cn.ubibi.jettyboot.framework.ioc;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
}
