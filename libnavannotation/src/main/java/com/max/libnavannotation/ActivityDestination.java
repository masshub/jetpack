package com.max.libnavannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by Maker on 2020/11/24.
 * Description:
 */

@Target(ElementType.TYPE)
public @interface ActivityDestination {
    String pageUrl();

    boolean needLogin() default false;

    boolean asStarter() default false;


}
