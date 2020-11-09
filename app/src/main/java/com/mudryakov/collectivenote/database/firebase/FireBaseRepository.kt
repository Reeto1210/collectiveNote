package com.mudryakov.collectivenote.database.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.utilits.AUTH
import com.mudryakov.collectivenote.utilits.CURRENT_UID
import com.mudryakov.collectivenote.utilits.DATABASE_REF
import com.mudryakov.collectivenote.utilits.logIn

class FireBaseRepository : AppDatabaseRepository {


    override fun connectToDatabase(type:String,onSucces:()->Unit) {
        logIn(type){onSucces()}
    }




    override fun addNewPayment(onSucces:()->Unit) {
        TODO("Not yet implemented")
    }

    override fun changeName(onSucces:()->Unit) {
        TODO("Not yet implemented")
    }

    override fun addnewQuest(onSucces:()->Unit) {
        TODO("Not yet implemented")
    }

    override fun getQuest(onSucces:()->Unit) {
        TODO("Not yet implemented")
    }

}