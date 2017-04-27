package com.yifan.butterfly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BActivity {
    /**
     * If the started activity has results (i.e., can be started with startActivityForResult())
     */
    boolean hasResult() default false;
}
