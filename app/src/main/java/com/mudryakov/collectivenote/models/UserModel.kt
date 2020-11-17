package com.mudryakov.collectivenote.models

data class UserModel(
    val firebaseId: String = "",
    var name: String = "",
    var roomId:String ="",
    var totalPayAtCurrentRoom:String =""

    ){
    override fun equals(other: Any?): Boolean {
        if (other !is UserModel) return false
        return this.firebaseId == other.firebaseId
    }  }