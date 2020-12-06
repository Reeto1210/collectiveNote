package com.mudryakov.collectivenote.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import com.mudryakov.collectivenote.database.RoomDatabase.RoomtimeConverter

@Entity(tableName = "AllPayments")
@TypeConverters(RoomtimeConverter::class)
data class PaymentModel(
    @PrimaryKey var firebaseId: String = "",
    @ColumnInfo val summ: String = "",
    @ColumnInfo val description: String = "",
    @ColumnInfo var time: Any = "",
    @ColumnInfo var fromId: String = "",
    @ColumnInfo  var imageUrl: String = "empty",
    @ColumnInfo var fromName: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (other !is PaymentModel) return false
        return this.firebaseId == other.firebaseId
    }
}

