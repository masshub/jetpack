package com.max.network;

import java.util.Map;

import okhttp3.FormBody;

/**
 * @author: maker
 * @date: 2020/11/30 17:26
 * @description:
 */
public class PostRequest<T> extends Request<T, PostRequest> {

    public PostRequest(String mUrl) {
        super(mUrl);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            bodyBuilder.add(entry.getKey(),String.valueOf(entry.getValue()));
        }
        okhttp3.Request request = builder.url(mUrl).post(bodyBuilder.build()).build();
        return request;
    }
}