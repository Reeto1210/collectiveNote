package com.mudryakov.collectivenote.screens.roomInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentRoomInfoBinding
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utilits.*


class RoomInfoFragment : BaseFragmentBack() {
    private var _Binding: FragmentRoomInfoBinding? = null
    private val mBinding get() = _Binding!!
    private lateinit var mViewModel: RoomInfoViewModel
    private lateinit var mObserverPayments: Observer<List<PaymentModel>>
    private lateinit var mObserverMembers: Observer<List<UserModel>>
    private var totalSumm = 0L
    private var totalMembers = 0
    private var totalValueOfPayments = 0
    var callback1 = false
    var callback2 = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentRoomInfoBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
        initObservers()
    }

    private fun initObservers() {
        mObserverMembers = Observer {
            mViewModel.allPayments.removeObserver { mObserverPayments }
            it.forEach { member ->
                totalSumm += member.totalPayAtCurrentRoom.toLong()
                totalMembers++
                callback2 = true
            }
            updateInfo()
        }
        mObserverPayments = Observer {
            it.forEach { _ ->
                totalValueOfPayments++
            }
            callback1 = true
            updateInfo()
        }
        mViewModel.allMembers.observe(this, mObserverMembers)
        mViewModel.allPayments.observe(this, mObserverPayments)

    }

    private fun initialization() {
        APP_ACTIVITY.title = getString(R.string.room_info)
        mViewModel = ViewModelProvider(this).get(RoomInfoViewModel::class.java)
        mBinding.infoIconPass.setImageResource(R.drawable.info_pass_icon)
        mBinding.infoIconPass.makeVisible()
        mBinding.infoIconPass.setOnClickListener {
            checkInternetConnection({ showNoInternetToast() }) {
                mBinding.infoIconPass.setImageResource(R.drawable.loading_pass)
                mViewModel.remindRoomPassword {
                    mBinding.settingsRoomPass.text = getString(R.string.room_pass, it)
                    mBinding.infoIconPass.makeInvisible()
                }
            }
        }
    }

    private fun updateInfo() {
        if (callback1 == callback2) {
            mBinding.settingsRoomTotalPayed.text =
                getString(R.string.total_sum_payed, totalSumm, ROOM_CURRENCY)
            mBinding.settingsRoomName.text =
                getString(R.string.room_name, AppPreference.getRoomName())
            mBinding.settingsRoomMembersValue.text =
                getString(R.string.total_members_ar_room, totalMembers)
            mBinding.settingsTotalValueOfPayments.text =
                getString(R.string.total_value_of_payments, totalValueOfPayments)
            mBinding.settingsUserTotalPayment.text =
                getString(R.string.total_user_payed, AppPreference.getTotalSumm(), ROOM_CURRENCY)
            mBinding.settingsRoomPass.text = getString(R.string.room_pass, "")
            mBinding.settingsCurrency.text = getString(R.string.room_current, ROOM_CURRENCY)
            mBinding.progressBar.makeInvisible()
            mBinding.settingsInfoLayout.makeVisible()
        }
    }

    override fun onDestroyView() {
        _Binding = null
        mViewModel.allPayments.removeObserver { mObserverPayments }
        mViewModel.allMembers.removeObserver { mObserverMembers }
        super.onDestroyView()
    }
}