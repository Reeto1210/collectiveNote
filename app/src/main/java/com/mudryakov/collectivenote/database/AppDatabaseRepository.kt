package com.mudryakov.collectivenote.database

interface AppDatabaseRepository {
fun connectToDatabase(type:String,onSucces:()->Unit)
    fun addNewPayment(onSucces:()->Unit)
    fun changeName(name:String,onSucces:()->Unit)
    fun addnewQuest(onSucces:()->Unit)
    fun getQuest(onSucces:()->Unit)
}