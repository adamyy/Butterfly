package com.yifan.butterfly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BExtra {
    /**
     * Optional, by default it is the identifier of the variable
     * @return The alias for your extra
     */
    String value() default "";
}
