package com.mudryakov.collectivenote.screens.mainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_ROOM_UID
import com.mudryakov.collectivenote.databinding.FragmentMainBinding
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.USER
import com.mudryakov.collectivenote.utilits.appPreference
import com.mudryakov.collectivenote.utilits.fastNavigate


class MainFragment : Fragment() {
    var _Binding: FragmentMainBinding? = null
    val mBinding get() = _Binding!!
    private lateinit var mViewModel: MainFragmentViewModel
    private lateinit var mObserver: Observer<List<UserModel>>
    private lateinit var mRecycle: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    var mAdapter: MainRecycleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        initialization()
        drawContent()
        initDrawer()
    }

    private fun initDrawer() {
        APP_ACTIVITY.back = false
        APP_ACTIVITY.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.actionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

    }

    private fun drawContent() {
        mObserver = Observer { list ->
            var totalSum = 0L
            list.forEach {
                totalSum += it.totalPayAtCurrentRoom.toLong()
                mAdapter?.addItem(it)
            }
            mBinding.loadingLayout.visibility = View.GONE
            mBinding.mainFragmentTotalPaymentRoom.text =
                getString(R.string.total_sum_payed, totalSum)
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
        CURRENT_ROOM_UID = appPreference.getRoomId()
        mViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        initRecycle()
        mBinding.mainAddNewPayment.setOnClickListener { fastNavigate(R.id.action_mainFragment_to_newPaymentFragment) }
    }


    private fun initRecycle() {
        mRecycle = mBinding.fragmentMainRecycle
        mAdapter = MainRecycleAdapter()
        mLayoutManager = LinearLayoutManager(this.context)
        mRecycle.adapter = mAdapter
        mRecycle.layoutManager = mLayoutManager

    }

    override fun onDestroyView() {
        mAdapter = null
        _Binding = null
        mViewModel.allMembers.removeObserver(mObserver)
        APP_ACTIVITY.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        super.onDestroyView()

    }


}
