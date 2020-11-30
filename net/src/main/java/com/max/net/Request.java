package com.max.net;

import androidx.annotation.IntDef;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import sun.rmi.runtime.Log;


/**
 * @author: maker
 * @date: 2020/11/30 16:29
 * @description:
 */
public abstract class Request<T, R> {
    protected String mUrl;
    protected HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();

    public static final int CACHE_ONLY = 1;
    public static final int CACHE_FIRST = 2;
    public static final int NET_ONLY = 3;
    // 访问网络成功后放入缓存
    public static final int NET_CACHE = 4;
    public String cacheKey;
    private Type mType;
    private Class mClaz;

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    public @interface CatchStrategy {
    }

    public Request(String mUrl) {
        this.mUrl = mUrl;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R addParam(String key, Object value) {
        try {
            Field file = value.getClass().getField("TYPE");
            Class claz = (Class) file.get(null);
            if (claz.isPrimitive()) {
                params.put(key, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R) this;
    }

    public R cacheKey(String key) {
        this.cacheKey = key;
        return (R) this;
    }

    public R responseType(Type type) {
        mType = type;
        return (R) this;
    }


    public R responseType(Class claz) {
        mClaz = claz;
        return (R) this;
    }

    public void execute(JsonCallback<T> callback) {
        getCall().enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ApiResponse<T> response = new ApiResponse();
                response.message = e.getMessage();
                callback.onError(response);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ApiResponse<T> apiResponse = parseResponse(response, callback);
                if (apiResponse.success) {
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onError(apiResponse);
                }
            }
        });

    }

    private ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();

        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;


        try {
            String content = response.body().string();
            if (success) {
                if (callback != null) {
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (mType != null) {
                    result.body = (T) convert.convert(content, mType);
                } else if (mClaz != null) {
                    result.body = (T) convert.convert(content, mClaz);
                } else {
//                    Log.e("request","数据无法解析");
                }
            } else {
                content = message;
            }

        } catch (IOException e) {
            message = e.getMessage();
            success = false;
        }

        result.status = status;
        result.message = message;
        result.success = success;
        return result;
    }

    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry :
                headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());

        }
    }

    public void execute() {
    }
}