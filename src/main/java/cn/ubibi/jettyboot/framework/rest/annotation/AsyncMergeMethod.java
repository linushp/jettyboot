package cn.ubibi.jettyboot.framework.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 使用Async的方式，如果方法耗时比较大
 * 对于相同的两个请求，只要要第一个请求返回，则后面的请求也会一起返回。
 * 提高了并发量。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncMergeMethod {
    int[] paramKey() default {};
}
