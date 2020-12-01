package com.max.network.cache;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * @author: maker
 * @date: 2020/12/1 13:50
 * @description:
 */
public class CacheConvert {
    @TypeConverter
    public static Long date2Long(Date date){
        return date.getTime();
    }

    @TypeConverter
    public static Date long2Date(Long date){
        return new Date(date);
    }


} 