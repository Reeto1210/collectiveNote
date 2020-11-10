package com.mudryakov.collectivenote.database.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.utilits.*

class FireBaseRepository : AppDatabaseRepository {


    override fun connectToDatabase(type:String,onSucces:()->Unit) {
        logIn(type){onSucces()}
    }


    override fun addNewPayment(onSucces:()->Unit) {
        TODO("Not yet implemented")
    }

    override fun changeName(name:String, onSucces:()->Unit) {
        DATABASE_REF.child(CURRENT_UID).child(CHILD_NAME).setValue(name)
            .addOnSuccessListener { onSucces() }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    override fun addnewQuest(onSucces:()->Unit) {
        TODO("Not yet implemented")
    }

    override fun getQuest(onSucces:()->Unit) {
        TODO("Not yet implemented")
    }

}