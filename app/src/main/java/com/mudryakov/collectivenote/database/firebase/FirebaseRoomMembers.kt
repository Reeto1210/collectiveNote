package com.mudryakov.collectivenote.database.firebase

import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.AppValueEventListener
import com.mudryakov.collectivenote.utilits.addMySingleListener


class FirebaseRoomMembers : LiveData<List<UserModel>>() {

    val mutableListOFUsers = mutableListOf<UserModel>()
    lateinit var list: List<String>

    val listener = AppValueEventListener {
        mutableListOFUsers.clear()

        REF_DATABASE_ROOT.child(NODE_ROOM_MEMBERS).child(CURRENT_ROOM_UID)
            .addMySingleListener{DataSnapShot ->
                list = DataSnapShot.children.map { sn-> sn.value.toString() }
                list.forEach { id ->
                    getUserFromId(id)
                }
            }
    }

    private fun getUserFromId(id: String) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(id)
            .addMySingleListener { userSnap ->
                val currentUser = (userSnap.getValue(UserModel::class.java) ?: UserModel())
                mutableListOFUsers.remove(currentUser)
                REF_DATABASE_ROOT.child(NODE_USERS).child(currentUser.firebaseId).child(
                    CHILD_TOTAL_PAY).child(
                   CURRENT_ROOM_UID
                ).addMySingleListener {
                   currentUser.totalPayAtCurrentRoom = if (it.value != null) it.value.toString() else "0"
                    mutableListOFUsers.add(currentUser)
                    if (mutableListOFUsers.size == list.size){ value = mutableListOFUsers}

                }
                }
    }


    override fun onActive() {
      REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(CURRENT_ROOM_UID).addValueEventListener(listener)
        super.onActive()

    }

    override fun onInactive() {
        REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(CURRENT_ROOM_UID).removeEventListener(listener)
        super.onInactive()
    }

}