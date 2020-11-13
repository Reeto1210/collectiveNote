package com.mudryakov.collectivenote.models

data class PaymentModel(
    var firebaseId: String = "",
    val summ: String = "",
    val description: String = "",
    var time: Any? = "",
    val fromId: String = "",
    var imageUrl: String = "empty"
)
