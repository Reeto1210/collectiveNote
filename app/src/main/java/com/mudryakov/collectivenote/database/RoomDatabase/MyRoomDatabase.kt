package com.mudryakov.collectivenote.database.RoomDatabase

import android.content.Context
import androidx.room.Database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import javax.inject.Inject
import javax.inject.Singleton

@Database(entities = [PaymentModel::class, UserModel::class], version = 1)
@Singleton
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun getDao(): myDao

    companion object {
        @Volatile
        private var instance: MyRoomDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): MyRoomDatabase {
            return if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext, MyRoomDatabase::class.java, "MyRoomDatabase"
                ).build()
                instance as MyRoomDatabase
            } else instance as MyRoomDatabase
        }
    }


}