package com.mudryakov.collectivenote.database.firebase

import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.utilits.*

class FireBaseRepository : AppDatabaseRepository {


    override fun connectToDatabase(type: String, onSucces: () -> Unit) {
        logIn(type) { onSucces() }
    }


    override  fun createNewRoom(roomName: String, roomPass: String, onSucces: () -> Unit) {
        REF_DATABASE_ROOT.child(NODE_ROOM_NAMES).addListenerForSingleValueEvent(
            AppValueEventListener { DataSnapshot ->
                if (DataSnapshot.hasChild(roomName)) {
                    showToast(APP_ACTIVITY.getString(R.string.this_namy_busy))
                } else {
                    pushRoomToFirebase(roomName, roomPass) {
                        updateUserRoomId(it) {

                            onSucces()
                        }
                    }
                }
            }
        )
    }

    override  fun joinRoom(roomName: String, roomPass: String, onSucces: () -> Unit) {
        lateinit var tryingId: String
        REF_DATABASE_ROOT.child(NODE_ROOM_NAMES).child(roomName)
            .addListenerForSingleValueEvent(AppValueEventListener {
                tryingId = it.value.toString()
                REF_DATABASE_ROOT.child(NODE_ROOM_DATA).child(tryingId).child(CHILD_PASS)
                    .addListenerForSingleValueEvent(AppValueEventListener { DataSnapshot ->
                        if (DataSnapshot.value == roomPass) {
                            updateUserRoomId(tryingId) {
                                        onSucces()
                            }
                        } else {
                            showToast(APP_ACTIVITY.getString(R.string.check_room_acc))
                        }
                    }
                    )
            })
    }


    override  fun addNewPayment(onSucces: () -> Unit) {
        TODO("Not yet implemented")
    }

    override  fun changeName(name: String, onSucces: () -> Unit) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_NAME).setValue(name)
            .addOnSuccessListener {
                appPreference.setUserId(CURRENT_UID)
                appPreference.setSignIn(true)
                appPreference.setName(name)
                onSucces()
            }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    override  fun addnewQuest(onSucces: () -> Unit) {
        TODO("Not yet implemented")
    }

    override  fun getQuest(onSucces: () -> Unit) {
        TODO("Not yet implemented")
    }

}