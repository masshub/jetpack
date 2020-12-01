package com.max.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author: maker
 * @date: 2020/11/30 17:09
 * @description:
 */
public class UrlCreator {
    public static String createUrlFromParams(String url, Map<String,Object> params){
        StringBuilder builder = new StringBuilder(url);
        if(url.indexOf("?") > 0 || url.indexOf("&") > 0){
            builder.append("&");
        } else {
            builder.append("?");
        }

        for (Map.Entry<String,Object> entry: params.entrySet()) {
            try {
                String value = URLEncoder.encode(String.valueOf(entry.getValue()),"UTF-8");
                builder.append(entry.getKey()).append("=").append(value).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
} 