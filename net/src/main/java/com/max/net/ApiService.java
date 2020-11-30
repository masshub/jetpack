package com.max.net;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import sun.net.www.protocol.https.DefaultHostnameVerifier;

/**
 * @author: maker
 * @date: 2020/11/30 16:03
 * @description:
 */
public class ApiService {
    protected static final OkHttpClient okHttpClient;
    protected static String sBaseUrl;
    protected static Convert sConvert;

    static {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


         okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();


        TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        try {
            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null,trustManagers,new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    public static void init(String baseUrl,Convert convert){
        sBaseUrl = baseUrl;
        if(convert == null){
            convert = new JsonConvert();

        }
        sConvert = convert;
    }
} 