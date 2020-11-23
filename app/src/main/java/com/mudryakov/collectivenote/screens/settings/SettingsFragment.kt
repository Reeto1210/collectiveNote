package com.mudryakov.collectivenote.screens.settings

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentSettingsBinding
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.appPreference
import com.mudryakov.collectivenote.utilits.restartActivity


class SettingsFragment : Fragment() {
    private var _Binding: FragmentSettingsBinding? = null
    private val mBinding get() = _Binding!!
    private lateinit var mViewModel: SettingsViewModel
    private lateinit var mObserverPayments: Observer<List<PaymentModel>>
    private lateinit var mObserverMembers: Observer<List<UserModel>>
    var totalSumm = 0L
    var totalMembers = 0
    var totalValueOfPayments = 0
    var callback1 = false
    var callback2 = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Binding = FragmentSettingsBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        mViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        setHasOptionsMenu(true)
        initialization()
        mViewModel.allMembers.observe(this, mObserverMembers)
        mViewModel.allpayments.observe(this, mObserverPayments)

    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        mObserverPayments = Observer {
            it.forEach { _ ->
                totalValueOfPayments++
            }
            callback1 = true
            updateInfo()
        }

        mObserverMembers = Observer {
            mViewModel.allpayments.removeObserver { mObserverPayments }
            it.forEach { member ->
                totalSumm += member.totalPayAtCurrentRoom.toLong()
                totalMembers++
                callback2 = true
            }
            updateInfo()
        }


    }

    private fun updateInfo() {
        if (callback1 == callback2) {
            mBinding.settingsRoomTotalPayed.text = getString(R.string.total_sum_payed, totalSumm)
            mBinding.settingsRoomName.text =
                getString(R.string.room_name, appPreference.getRoomName())
            mBinding.settingsCurrentName.text =
                getString(R.string.user_name, appPreference.getUserName())
            mBinding.settingsRoomMembersValue.text =
                getString(R.string.total_members_ar_room, totalMembers)
            mBinding.settingsTotalValueOfPayments.text =
                getString(R.string.total_value_of_payments, totalValueOfPayments)
            mBinding.settingsUserTotalPayment.text =
                getString(R.string.total_user_payed, appPreference.getTotalSumm())
            mBinding.progressBar.visibility = View.GONE
            mBinding.settingsInfoLayout.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_room -> changeRoom()
            R.id.exit_acc -> changeUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeUser() {
        mViewModel.signOut()
        appPreference.clear()
        restartActivity()
    }

    fun changeRoom() {
        appPreference.setRoomId("fail")
        appPreference.setSignInRoom(false)
        appPreference.setTotalSumm("0")
        restartActivity()
    }

    override fun onDestroyView() {
        _Binding = null
        mViewModel.allpayments.removeObserver { mObserverPayments }
        mViewModel.allMembers.removeObserver { mObserverMembers }
        super.onDestroyView()
    }

}