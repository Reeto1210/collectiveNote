package com.mudryakov.collectivenote.utilits

import com.mudryakov.collectivenote.MainActivity
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel




const val CREATOR ="creator"
const val MEMBER = "member"
const val SIGN_CODE_REQUEST = 1488
const val TYPE_GOOGLE_ACCOUNT = "googleAcc"
const val TYPE_EMAIL = "emailAcc"
var INTERNET = true

lateinit var USER: UserModel
var ROOM_CURRENCY = ""
lateinit var APP_ACTIVITY:MainActivity
