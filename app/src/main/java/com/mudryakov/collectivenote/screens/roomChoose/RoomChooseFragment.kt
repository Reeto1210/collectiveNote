package com.mudryakov.collectivenote.screens.roomChoose

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.databinding.FragmentRoomChooseBinding
import com.mudryakov.collectivenote.utility.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard
import java.util.*


class RoomChooseFragment : Fragment() {
    var _Binding: FragmentRoomChooseBinding? = null
    val mBinding get() = _Binding!!
    lateinit var mViewModel: RoomChooseViewModel
    var messageText = ""
    var currencySign = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentRoomChooseBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        hideKeyboard(APP_ACTIVITY)
        initialization()
        if (AppPreference.getSignInRoom()) {
            CURRENT_UID = AppPreference.getUserId()
            navNext()
        } else {
            buildRoomChooseDialog {enterRoom(it)}
        }
    }

    private fun initSpinner() {
        val spinner = mBinding.roomChooseSpinner
        val currency = resources.getStringArray(R.array.currency_array)
        val adapter = MyArrayAdapter(APP_ACTIVITY, R.layout.spinner_item, currency)
        spinner.adapter = adapter
        spinner.makeVisible()
           spinner.onItemSelectedListener = AppOnItemSelectedListener { position ->
            currencySign = when (position) {
                1 -> getString(R.string.RUB)
                2 -> getString(R.string.USD)
                3 -> getString(R.string.EUR)
                else -> ""
            }
       }
    }

    private fun initialization() {
        APP_ACTIVITY.title = AppPreference.getUserName()
        mViewModel = ViewModelProvider(APP_ACTIVITY).get(RoomChooseViewModel::class.java)
    }

    private fun enterRoom(userType: String) {
        when (userType) {
            CREATOR -> createRoom()
            else -> joinRoom()
        }
    }

    private fun createRoom() {
        initSpinner()
        APP_ACTIVITY.title = getString(R.string.create_room)
        mBinding.roomChooseContinue.setOnClickListener {
            val roomName = mBinding.roomChooseName.text.toString().toLowerCase(Locale.ROOT)
            val roomPass = mBinding.roomChoosePassword.text.toString().toLowerCase(Locale.ROOT)
            when {
                roomName.isEmpty() || roomPass.isEmpty() -> showToast(R.string.add_info)
                currencySign == "" -> showToast(R.string.choose_currency)
                else -> {
                    showProgressBar()
                    checkInternetAtAuth({onFail()}) {
                        mViewModel.createRoom(roomName, roomPass, currencySign, { onFail() }) {
                            AppPreference.setRoomName(roomName)
                            AppPreference.setCurrency(currencySign)
                            messageText = getString(R.string.toast_create_room, roomName)
                            navNext()
                        }
                    }
                }
            }
        }
    }

    private fun joinRoom() {
        mBinding.roomChooseSpinner.makeGone()
        APP_ACTIVITY.title = getString(R.string.join_room)
        mBinding.roomChooseContinue.setOnClickListener {
            showProgressBar()
            val roomName = mBinding.roomChooseName.text.toString().toLowerCase(Locale.ROOT)
            val roomPass = mBinding.roomChoosePassword.text.toString().toLowerCase(Locale.ROOT)
            checkInternetAtAuth({onFail()}){
                mViewModel.joinRoom(roomName, roomPass, { onFail() }) {
                    AppPreference.setRoomName(roomName)
                    messageText = getString(R.string.toast_join_room, roomName)
                    navNext()
                }
            }
        }
    }

    private fun navNext() {
        hideKeyboard(APP_ACTIVITY)
        if (!AppPreference.getSignInRoom()) Toast.makeText(APP_ACTIVITY,messageText,Toast.LENGTH_LONG).show()
        AppPreference.setSignInRoom(true)
        fastNavigate(R.id.action_roomChooseFragment_to_mainFragment)
    }

    private fun showProgressBar() {
        hideKeyboard(APP_ACTIVITY)
        mBinding.roomChooseProgressBar.makeVisible()
        mBinding.roomChooseContinue.makeGone()
    }

    private fun onFail() {
        mBinding.roomChooseProgressBar.makeGone()
        mBinding.roomChooseContinue.makeVisible()
    }
}