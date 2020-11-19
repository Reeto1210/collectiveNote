package com.mudryakov.collectivenote.database.RoomDatabase

import androidx.room.TypeConverter
import java.util.stream.Collectors

class RoomtimeConverter {
    @TypeConverter
    fun fromTimeString(time: Any): String {
        return time.toString()
    }

@TypeConverter
fun toTimeAny(time: String):Any{
    return time as Any
}

}

