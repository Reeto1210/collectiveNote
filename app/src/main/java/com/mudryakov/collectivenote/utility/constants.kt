package com.mudryakov.collectivenote.utility

import com.mudryakov.collectivenote.MainActivity
import com.mudryakov.collectivenote.models.UserModel
var DRAWER_ENABLED = false
const val CREATOR ="creator"
const val MEMBER = "member"
const val SIGN_CODE_REQUEST = 1488
const val TYPE_GOOGLE_ACCOUNT = "googleAcc"
const val TYPE_EMAIL = "emailAcc"
lateinit var USER: UserModel
var ROOM_CURRENCY = AppPreference.getCurrency()
lateinit var APP_ACTIVITY:MainActivity
const val EMPTY = "empty"
const val CROP_IMAGE_SIZE = 900
const val FAIL = "fail"
const val BUNDLE_ID = "id"
const val BUNDLE_NAME = "name"