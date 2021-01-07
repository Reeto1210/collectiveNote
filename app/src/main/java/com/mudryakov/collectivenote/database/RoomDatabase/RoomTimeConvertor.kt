package com.mudryakov.collectivenote.database.RoomDatabase

import androidx.room.TypeConverter
import javax.inject.Inject

class RoomtimeConverter @Inject constructor() {

    @TypeConverter
    fun fromTimeString(time: Any): String {
        return time.toString()
    }

    @TypeConverter
    fun toTimeAny(time: String): Any {
        return time
    }

}

