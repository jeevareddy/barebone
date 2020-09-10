package com.barebone.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ModelUser::class], version = 1)
abstract class Barebonedb : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        var INSTANCE: Barebonedb? = null

        fun getAppDatabase(context: Context): Barebonedb? {
            if (INSTANCE == null) {
                synchronized(Barebonedb::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        Barebonedb::class.java,
                        "barebonedb"
                    ).allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}