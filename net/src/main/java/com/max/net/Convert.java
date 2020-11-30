package com.max.net;

import java.lang.reflect.Type;

/**
 * @author: maker
 * @date: 2020/11/30 17:42
 * @description:
 */
public interface Convert<T> {
    T convert(String response, Type type);

    T convert(String response,Class claz);


}
