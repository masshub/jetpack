package com.max.network.cache;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.max.common.AppGlobals;

/**
 * @author: maker
 * @date: 2020/12/1 10:31
 * @description:
 */
@Database(entities = {Cache.class},version = 1,exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase database;

    static {
        // 创建内存数据库,进程被杀，数据消失
//        Room.inMemoryDatabaseBuilder()
        database = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "jetpack_cache")
                // 是否运行在主线程中进行查询
//                .allowMainThreadQueries()
//                .addCallback()
                // 查询线程池
//                .setQueryExecutor()
                //
//                .openHelperFactory()
                // 日志模式
//                .setJournalMode()
                // 升级异常是否回滚
//                .fallbackToDestructiveMigration()
                // 山脊异常回滚指定版本号
//                .fallbackToDestructiveMigrationFrom()
                //
//                .addMigrations(CacheDatabase.sMigration)
                .build();
    }

    public static CacheDatabase get(){
        return database;
    }


    public abstract CacheDao getCache();


//    static Migration sMigration = new Migration(1, 3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("alter table teacher rename to student");
//            database.execSQL("alter table teacher add column teacher_age INTEGER NOT NULL default 0");
//        }
//    };

} 