package com.mudryakov.collectivenote.utilits

import android.content.Context
import android.content.SharedPreferences

object appPreference {
    private const val FAIL = "fail"
    private const val namePref = "preference"
    private const val USER_ID = "userId"
    private const val USER_NAME = "userName"
    private const val IS_SIGN_IN = "isSignIn"
    private const val IS_SIGN_IN_ROOM = "isSignInRoom"
    private const val ROOM_ID = "roomId"


    private lateinit var mPreference: SharedPreferences

    fun getPreference(context: Context): SharedPreferences {
        mPreference = context.getSharedPreferences(namePref, Context.MODE_PRIVATE)
        return mPreference
    }

    fun setUserId(id: String) {
        mPreference.edit()
            .putString(USER_ID, id)
            .apply()
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

}

