package com.mudryakov.collectivenote.utilits

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mudryakov.collectivenote.MainActivity
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.models.User

const val SIGN_CODE_REQUEST = 1488
const val TYPE_GOOGLE_ACCOUNT = "googleAcc"
const val TYPE_EMAIL = "emailAcc"
lateinit var EMAIL:String
lateinit var PASSWORD:String
lateinit var USER: User

lateinit var APP_ACTIVITY:MainActivity
