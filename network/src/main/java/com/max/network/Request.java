package com.max.network;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.max.network.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * @author: maker
 * @date: 2020/11/30 16:29
 * @description:
 */
public abstract class Request<T, R extends Request> implements Cloneable {
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
    private int mCacheStrategy;

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
        if (value == null) {
            return (R) this;
        }
        //int byte char short long double float boolean 和他们的包装类型，但是除了 String.class 所以要额外判断
        try {
            if (value.getClass() == String.class) {
                params.put(key, value);
            } else {
                //反射获取Object的基本类型 TYPE:代表原始类型的实例
                Field field = value.getClass().getField("TYPE");
                Class claz = (Class) field.get(null);
                //如果claz是基本类型
                if (claz != null && claz.isPrimitive()) {
                    params.put(key, value);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R) this;
    }

    public R cacheKey(String key) {
        this.cacheKey = key;
        return (R) this;
    }

    public R cacheStrategy(@CatchStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
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


    /**
     * 同步请求
     *
     * @return
     */
    public ApiResponse<T> execute() {
        if (mCacheStrategy == CACHE_ONLY) {
            return readCache();
        }
        try {
            Response response = getCall().execute();
            ApiResponse<T> result = parseResponse(response, null);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 异步请求
     *
     * @param callback
     */
    @SuppressLint("RestrictedApi")
    public void execute(final JsonCallback<T> callback) {
        if (mCacheStrategy != NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if (callback != null) {
                        callback.onCacheSuccess(response);
                    }

                }
            });
        }
        if (mCacheStrategy != CACHE_ONLY) {
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

    }

    /**
     * 读取缓存
     *
     * @return
     */
    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.status = 304;
        result.message = "缓存获取成功";
        result.success = true;
        result.body = (T) cache;

        return result;
    }

    /**
     * 解析返回数据
     *
     * @param response
     * @param callback
     * @return
     */
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
                    Log.e("request", "数据无法解析");
                }
            } else {
                message = content;
            }

        } catch (IOException e) {
            message = e.getMessage();
            success = false;
        }

        result.status = status;
        result.message = message;
        result.success = success;

        if (mCacheStrategy != NET_ONLY && result.success && result.body != null && result.body instanceof Serializable) {
            saveCache(result.body);

        }
        return result;
    }

    /**
     * 缓存数据
     *
     * @param body
     */
    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        CacheManager.save(key, body);

    }

    /**
     * 生成缓存key
     *
     * @return
     */
    private String generateCacheKey() {
        cacheKey = UrlCreator.createUrlFromParams(mUrl, params);
        return cacheKey;
    }


    /**
     * @return
     */
    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);


    /**
     * 添加header
     *
     * @param builder
     */
    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry :
                headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());

        }
    }


    @NonNull
    @Override
    public Request clone() throws CloneNotSupportedException {
        return (Request) super.clone();
    }

}