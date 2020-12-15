package com.mudryakov.collectivenote.screens.groupChoose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.databinding.FragmentGroupChooseBinding
import com.mudryakov.collectivenote.utility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard
import java.util.*


class GroupChooseFragment : Fragment() {
    var _Binding: FragmentGroupChooseBinding? = null
    val mBinding get() = _Binding!!
    lateinit var mViewModel: GroupChooseViewModel
    var messageText = ""
    var currencySign = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentGroupChooseBinding.inflate(layoutInflater)
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
            buildGroupChooseDialog { enterRoom(it) }

        }
    }

    private fun initSpinner() {
        val spinner = mBinding.groupChooseSpinner
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
        APP_ACTIVITY.back = true
        APP_ACTIVITY.title = AppPreference.getUserName()
        mViewModel = ViewModelProvider(APP_ACTIVITY).get(GroupChooseViewModel::class.java)
    }

    private fun enterRoom(userType: String) {
            when (userType) {
            CREATOR -> createGroup()
            else -> joinGroup()
        }
    }

    private fun createGroup() {
        initSpinner()
        APP_ACTIVITY.title = getString(R.string.create_room)
        mBinding.groupChooseContinue.setOnClickListener {
            val groupName = mBinding.groupChooseName.text.toString().toLowerCase(Locale.ROOT)
            val roomPass = mBinding.groupChoosePassword.text.toString().toLowerCase(Locale.ROOT)
            when {
                groupName.isEmpty() || roomPass.isEmpty() -> showToast(R.string.add_info)
                currencySign == "" -> showToast(R.string.choose_currency)
                else -> {
                    showProgressBar()
                    checkInternetAtAuth({ onFail() }) {
                        mViewModel.createGroup(groupName, roomPass, currencySign, { onFail() }) {
                            AppPreference.setGroupName(groupName)
                            AppPreference.setCurrency(currencySign)
                            messageText = getString(R.string.toast_create_group, groupName)
                            navNext()
                        }
                    }
                }
            }
        }
    }

    private fun joinGroup() {
        mBinding.groupChooseSpinner.makeGone()
        APP_ACTIVITY.title = getString(R.string.join_group)
        mBinding.groupChooseContinue.setOnClickListener {
            showProgressBar()
            val roomName = mBinding.groupChooseName.text.toString().toLowerCase(Locale.ROOT)
            val roomPass = mBinding.groupChoosePassword.text.toString().toLowerCase(Locale.ROOT)
            checkInternetAtAuth({ onFail() }) {
                mViewModel.joinGroup(roomName, roomPass, { onFail() }) {
                    AppPreference.setGroupName(roomName)
                    messageText = getString(R.string.toast_join_group, roomName)
                    navNext()
                }
            }
        }
    }

    private fun navNext() {
        hideKeyboard(APP_ACTIVITY)
        if (!AppPreference.getSignInRoom()) Toast.makeText(
            APP_ACTIVITY,
            messageText,
            Toast.LENGTH_LONG
        ).show()
        AppPreference.setSignInRoom(true)
        fastNavigate(R.id.action_groupChooseFragment_to_mainFragment)
    }

    private fun showProgressBar() {
        hideKeyboard(APP_ACTIVITY)
        mBinding.roomChooseProgressBar.makeVisible()
        mBinding.groupChooseContinue.makeGone()
    }

    private fun onFail() {
        mBinding.roomChooseProgressBar.makeGone()
        mBinding.groupChooseContinue.makeVisible()
    }
}