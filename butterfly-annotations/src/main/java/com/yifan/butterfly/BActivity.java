package com.yifan.butterfly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yifan on 17/2/23.
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface BActivity {
    /**
     * Optional, by default it is the class name
     * @return Alias for activity
     */
    String alias() default "";
}
