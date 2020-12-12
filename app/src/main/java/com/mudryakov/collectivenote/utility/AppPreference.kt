package com.mudryakov.collectivenote.utility

import android.content.Context
import android.content.SharedPreferences

object AppPreference {

    private const val namePref = "preference"
    private const val USER_ID = "userId"
    private const val USER_NAME = "userName"
    private const val IS_SIGN_IN = "isSignIn"
    private const val IS_SIGN_IN_ROOM = "isSignInRoom"
    private const val ROOM_ID = "roomId"
    private const val TOTAL_PAY = "totalPay"
    private const val ROOM_NAME = "roomName"
    private const val ROOM_CURRENCY = "roomCurrency"

    private lateinit var mPreference: SharedPreferences


    fun clear() {
        mPreference.edit().clear()
            .apply()
    }

    fun getPreference(context: Context): SharedPreferences {
        mPreference = context.getSharedPreferences(namePref, Context.MODE_PRIVATE)
        return mPreference
    }

       fun setUserId(id: String) {
        mPreference.edit()
            .putString(USER_ID, id)
            .apply()
    }

    fun setCurrency(currency: String) {
        mPreference.edit()
            .putString(ROOM_CURRENCY, currency)
            .apply()
    }

    fun getCurrency(): String {
        return mPreference.getString(ROOM_CURRENCY, FAIL).toString()
    }


    fun setName(userName: String) {
        mPreference.edit()
            .putString(USER_NAME, userName)
            .apply()
    }

    fun setRoomId(roomId: String) {
        mPreference.edit()
            .putString(ROOM_ID, roomId)
            .apply()
    }


    fun setSignIn(sign: Boolean) {
        mPreference.edit()
            .putBoolean(IS_SIGN_IN, sign)
            .apply()
    }

    fun getSignIn(): Boolean {
        return mPreference.getBoolean(IS_SIGN_IN, false)
    }

    fun setSignInRoom(sign: Boolean) {
        mPreference.edit()
            .putBoolean(IS_SIGN_IN_ROOM, sign)
            .apply()
    }

    fun getSignInRoom(): Boolean {

        return mPreference.getBoolean(IS_SIGN_IN_ROOM, false)
    }


    fun getUserId(): String {
        return mPreference.getString(USER_ID, FAIL).toString()
    }

    fun getUserName(): String {
        return mPreference.getString(USER_NAME, FAIL).toString()
    }

    fun getRoomId(): String {
        return mPreference.getString(ROOM_ID, FAIL).toString()
    }

    fun setTotalSumm(s: String) {
        mPreference.edit()
            .putString(TOTAL_PAY, s)
            .apply()
    }

    fun getTotalSumm(): String {
        return mPreference.getString(TOTAL_PAY, "0").toString()
    }

    fun setRoomName(name: String) {
        mPreference.edit().putString(ROOM_NAME, name)
            .apply()
    }

    fun getRoomName(): String {
        return mPreference.getString(ROOM_NAME, FAIL).toString()

    }


}


