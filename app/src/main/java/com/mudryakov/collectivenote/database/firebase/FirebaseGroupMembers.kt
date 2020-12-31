package com.mudryakov.collectivenote.database.firebase

import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.AppValueEventListener
import com.mudryakov.collectivenote.utility.addMySingleListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FirebaseGroupMembers : LiveData<List<UserModel>>() {

    private val mutableListOFUsers = mutableListOf<UserModel>()
    lateinit var list: List<String>

    private val listener = AppValueEventListener {
        mutableListOFUsers.clear()
        REF_DATABASE_ROOT.child(NODE_GROUP_MEMBERS).child(CURRENT_GROUP_UID)
            .addMySingleListener { DataSnapShot ->
                list = DataSnapShot.children.map { sn -> sn.value.toString() }
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
                    CHILD_TOTAL_PAY
                ).child(
                    CURRENT_GROUP_UID
                ).addMySingleListener {
                    currentUser.totalPayAtCurrentGroup =
                        if (it.value != null) it.value.toString() else "0.00"
                    mutableListOFUsers.add(currentUser)
                    if (mutableListOFUsers.size == list.size) {
                        CoroutineScope(Dispatchers.IO).launch {
                            ROOM_REPOSITORY.updateAllUsersRoomDatabase(mutableListOFUsers)
                        }
                        value = mutableListOFUsers
                    }
                }
            }
    }


    override fun onActive() {
        REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(CURRENT_GROUP_UID).addValueEventListener(listener)
        super.onActive()
    }

    override fun onInactive() {
        REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(CURRENT_GROUP_UID).removeEventListener(listener)
        super.onInactive()
    }
}