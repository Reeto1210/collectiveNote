package com.mudryakov.collectivenote.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AllUsers")
data class UserModel(
    @PrimaryKey val firebaseId: String = "",
    @ColumnInfo var name: String = "",
    @ColumnInfo var roomId:String ="",
    @ColumnInfo var totalPayAtCurrentRoom:String =""

    ){
    override fun equals(other: Any?): Boolean {
        if (other !is UserModel) return false
        return this.firebaseId == other.firebaseId
    }



}