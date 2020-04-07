package com.wt.galaxy.configuration.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limiter {

    /**
     * 从第一次访问接口的时间到cycle周期时间内，无法超过frequency次，默认60次
     * @return
     */
    int frequency() default 60;

    /**
     * 周期时间,默认30分钟内
     * @return
     */
    int cycle() default 30 * 60 * 1000;

    /**
     * 返回的错误信息
     * @return
     */
    String message() default "请求过于频繁,请稍后重试";
}

