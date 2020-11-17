package com.mudryakov.collectivenote.screens.mainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_ROOM_UID
import com.mudryakov.collectivenote.databinding.FragmentMainBinding
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.AppBottomSheetCallBack
import com.mudryakov.collectivenote.utilits.USER
import com.mudryakov.collectivenote.utilits.appPreference


class MainFragment : Fragment() {
    var _Binding: FragmentMainBinding? = null
    val mBinding get() = _Binding!!
    private lateinit var mViewModel: MainFragmentViewModel
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var mObserver: Observer<List<UserModel>>
    private lateinit var mRecycle: RecyclerView
    private lateinit var mAdapter: MainRecycleAdapter
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
    drawContent()
    }

    private fun drawContent() {
        initBottomSheetBehavior()
        mObserver = Observer { list ->
            var totalSumm = 0
            list.forEach {
                totalSumm += it.totalPayAtCurrentRoom.toInt()
                mAdapter.addItem(it)
            }
            mBinding.mainFragmentTotalPaymentRoom.text =
                getString(R.string.total_sum_payed, totalSumm)
        }
        mViewModel.allMembers.observe(this, mObserver)

    }

    private fun initialization() {
        APP_ACTIVITY.title = APP_ACTIVITY.getString(R.string.app_name)
        USER = UserModel(
            appPreference.getUserId(),
            appPreference.getUserName(),
            appPreference.getRoomId(),
            appPreference.getTotalSumm()

        )
        CURRENT_ROOM_UID = USER.roomId
        mViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        initRecycle()


    }

    private fun initRecycle() {
        mRecycle = mBinding.fragmentMainRecycle
        mAdapter = MainRecycleAdapter()
        mRecycle.adapter = mAdapter
        mRecycle.layoutManager = LinearLayoutManager(APP_ACTIVITY)
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
            APP_ACTIVITY.navConroller.navigate(R.id.action_mainFragment_to_historyFragment)
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

    override fun onDestroyView() {
        _Binding = null
        mViewModel.allMembers.removeObserver(mObserver)
        super.onDestroyView()
    }
}
