package com.mudryakov.collectivenote.screens.mainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_ROOM_UID
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.databinding.FragmentMainBinding
import com.mudryakov.collectivenote.models.User
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.AppBottomSheetCallBack
import com.mudryakov.collectivenote.utilits.USER
import com.mudryakov.collectivenote.utilits.appPreference


class MainFragment : Fragment() {
    var _Binding: FragmentMainBinding? = null
    val mBinding get() = _Binding!!
    private lateinit var mViewModel: MainFragmentViewModel
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Binding = FragmentMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
    }

    private fun initialization() {
        initBottomSheetBehavior()
        APP_ACTIVITY.title = APP_ACTIVITY.getString(R.string.app_name)
        mViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        USER =
            User(appPreference.getUserId(), appPreference.getUserName(), appPreference.getRoomId())
         CURRENT_ROOM_UID = USER.roomId

    }

    private fun initBottomSheetBehavior() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mBinding.include.bottomSheet)
        mBottomSheetBehavior.addBottomSheetCallback(
            AppBottomSheetCallBack(
                mBinding.mainAddNewPayment,
                mBinding.include.bottomSheetBtnArrow,
                mBinding.mainBack
            )
        )
        mBinding.include.bottomSheetBtnArrow.setOnClickListener {
            mBottomSheetBehavior.setState(
                if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED
                else BottomSheetBehavior.STATE_EXPANDED
            )
        }
        mBinding.include.bottomSheetHistory.setOnClickListener {
            APP_ACTIVITY.navConroller.navigate(
                R.id.action_mainFragment_to_historyFragment
            )
        }
        mBinding.include.bottomSheetGetQuest.setOnClickListener {
            APP_ACTIVITY.navConroller.navigate(
                R.id.action_mainFragment_to_questFragment
            )
        }
        mBinding.include.bottomSheetSettings.setOnClickListener { }
        mBinding.include.bottomSheetHelp.setOnClickListener { }
        mBinding.mainAddNewPayment.setOnClickListener { APP_ACTIVITY.navConroller.navigate(R.id.action_mainFragment_to_newPaymentFragment) }

    }
}