package com.mudryakov.collectivenote.models

data class PaymentModel(
    var firebaseId: String = "",
    val summ: String = "",
    val description: String = "",
    var time: Any = "",
    var fromId: String = "",
    var imageUrl: String = "empty",
    var fromName: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (other !is PaymentModel) return false
        return this.firebaseId == other.firebaseId
    }
}

