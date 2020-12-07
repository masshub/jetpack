package com.max.network;

/**
 * @author: maker
 * @date: 2020/11/30 17:07
 * @description:
 */
public class GetRequest<T> extends Request<T, GetRequest> {
    public GetRequest(String mUrl) {
        super(mUrl);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        String url = UrlCreator.createUrlFromParams(mUrl, params);
        okhttp3.Request request = builder.get().url(url).build();
        return request;
    }
}