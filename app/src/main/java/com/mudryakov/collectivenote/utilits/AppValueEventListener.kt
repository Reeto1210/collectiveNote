package com.mudryakov.collectivenote.utilits

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


fun DatabaseReference.addMySingleListener(function:(DataSnapshot)->Unit){
    this.addListenerForSingleValueEvent(AppValueEventListener{function(it)})
}
fun DatabaseReference.addListener(function:(DataSnapshot)->Unit){
    this.addValueEventListener(AppValueEventListener{function(it)})
}


class AppValueEventListener(val function:(DataSnapshot)->Unit): ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        function(snapshot)

    }

    override fun onCancelled(error: DatabaseError) {}
}