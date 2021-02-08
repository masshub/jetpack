package com.max.network.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.max.navigation.model.*;

/**
 * @author: maker
 * @date: 2020/12/1 14:09
 * @description:
 */
public class CacheManager {


    public static <T> void save(String key, T body) {
        Cache cache = new Cache();
        cache.key = key;
        cache.data = toByteArray(body);

        CacheDatabase.get().getCache().save(cache);
    }


    public static Object getCache(String key) {
        Cache cache = CacheDatabase.get().getCache().getCache(key);
        if (cache != null && cache.data != null) {
            return toObject(cache.data);
        }
        return null;
    }

    private static Object toObject(byte[] data) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        try {
            bais = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                }

                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return null;
    }


    private static <T> byte[] toByteArray(T body) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(body);
            oos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (baos != null) {
                    baos.close();
                }

                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return new byte[0];
    }

    public static <T> void delete(String keyUserCache, T cache) {
        Cache cache1 = new Cache();
        cache1.key = keyUserCache;
        cache1.data = toByteArray(cache);
        CacheDatabase.get().getCache().delete(cache1);

    }
}