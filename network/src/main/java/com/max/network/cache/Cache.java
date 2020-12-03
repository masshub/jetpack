package com.max.network.cache;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * @author: maker
 * @date: 2020/12/1 10:57
 * @description:
 */
@Entity(tableName = "cache")
public class Cache implements Serializable {
    @PrimaryKey
    @NonNull
    public String key;

    //    @ColumnInfo(name = "_data")
    public byte[] data;

} 