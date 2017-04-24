package com.yifan.butterfly;

import com.squareup.javapoet.ClassName;

/**
 * Created by yifan on 17/3/2.
 */

final class C {

    public static final String BUTTERFLY_PACKAGE_NAME = "com.yifan.butterfly";
    public static final String HELPER_PACKAGE_NAME = BUTTERFLY_PACKAGE_NAME + ".helper";
    public static final String BINDER_CLASS_SUFFIX = "$$Binder";
    public static final String HELPER_CLASS_SUFFIX = "$$Helper";
    public static final ClassName BUTTERFLY = ClassName.get("com.yifan.butterfly", "Butterfly");

    public static final ClassName ACTIVITY_HELPER = ClassName.get("com.yifan.butterfly", "ActivityHelper");
    public static final ClassName BINDER = ClassName.get("com.yifan.butterfly", "Binder");

    public static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    public static final ClassName ACTIVITY = ClassName.get("android.app", "Activity");
    public static final ClassName APPLICATION = ClassName.get("android.app", "Application");
    public static final ClassName VIEW = ClassName.get("android.view", "View");
    public static final ClassName ACTIVITY_OPTIONS_COMPAT = ClassName.get("android.support.v4.app", "ActivityOptionsCompat");
    public static final ClassName INTENT = ClassName.get("android.content", "Intent");
    public static final ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    public static final ClassName COMPONENT_NAME = ClassName.get("android.content", "ComponentName");

    public static final ClassName ILLEGALSTATE_EXCEPTION = ClassName.get("java.lang", "IllegalStateException");

}
