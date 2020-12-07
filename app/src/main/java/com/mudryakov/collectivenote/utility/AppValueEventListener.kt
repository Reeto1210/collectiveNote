package com.mudryakov.collectivenote.utility

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener



class AppValueEventListener(val function:(DataSnapshot)->Unit): ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        function(snapshot)
    }

    override fun onCancelled(error: DatabaseError) {}
}

fun DatabaseReference.addMySingleListener(function:(DataSnapshot)->Unit){
    this.addListenerForSingleValueEvent(AppValueEventListener{function(it)})
}