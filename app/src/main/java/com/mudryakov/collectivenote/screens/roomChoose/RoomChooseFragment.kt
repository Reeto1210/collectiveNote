package com.mudryakov.collectivenote.screens.roomChoose

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.USERNAME
import com.mudryakov.collectivenote.databinding.FragmentRoomChooseBinding
import com.mudryakov.collectivenote.utilits.*


class roomChooseFragment : Fragment() {
    var _Binding: FragmentRoomChooseBinding? = null
    val mBinding get() = _Binding!!
    lateinit var mViewModel: RoomChooseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Binding = FragmentRoomChooseBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()

        if (appPreference.getSignInRoom()) {
            showToast(getString(R.string.toast_join_room) + appPreference.getRoomId())
            navNext()
        } else {

        val builder = AlertDialog.Builder(this.context)
        builder.setTitle(getString(R.string.choose_room_alert_dialog_title))
            .setMessage(getString(R.string.choose_room_alert_dialog_text))
            .setPositiveButton(getString(R.string.choose_room_alert_dialog_possitive_button)) { _: DialogInterface, _: Int ->
                enterRoom(
                    CREATOR
                )
            }
            .setNeutralButton(getString(R.string.choose_room_alert_dialog_negative_button)) { _: DialogInterface, _: Int ->
                enterRoom(
                    MEMBER
                )
            }
            .setCancelable(false)
            .show()
    }
    }

    private fun initialization() {
        APP_ACTIVITY.title = USERNAME
        mViewModel = ViewModelProvider(APP_ACTIVITY).get(RoomChooseViewModel::class.java)

    }

    private fun enterRoom(userType: String) {
        when (userType) {
            CREATOR -> createRoom()
            else -> joinRoom()
        }
    }

    private fun createRoom() {
        APP_ACTIVITY.title = getString(R.string.create_room)
        mBinding.roomChooseContinue.setOnClickListener {
            val roomName = mBinding.roomChooseName.text.toString()
            val roomPass = mBinding.roomChoosePassword.text.toString()
            mViewModel.createRoom(roomName, roomPass) {
                showToast(getString(R.string.toast_room_create_room) + roomName + getString(R.string.toast_create_room_maded))
                navNext()
            }
        }
    }


    private fun joinRoom() {
        APP_ACTIVITY.title = getString(R.string.join_room)
        mBinding.roomChooseContinue.setOnClickListener {
            val roomName = mBinding.roomChooseName.text.toString()
            val roomPass = mBinding.roomChoosePassword.text.toString()
            mViewModel.joinRoom(roomName, roomPass) {
                showToast(getString(R.string.toast_join_room) + roomName)
                navNext()
            }
        }
    }

    private fun navNext() {
        APP_ACTIVITY.navConroller.navigate(R.id.action_roomChooseFragment_to_mainFragment)
    }
}