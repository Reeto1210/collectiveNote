package com.mudryakov.collectivenote.models

data class PaymentModel(
    var firebaseId: String = "",
    val summ: String = "",
    val description: String = "",
    var time: Any = "",
    val fromId: String = "",
    var imageUrl: String = "empty"
){
    override fun equals(other: Any?): Boolean {
        if (other !is PaymentModel) return false
        return  this.firebaseId == other.firebaseId
    }
}
