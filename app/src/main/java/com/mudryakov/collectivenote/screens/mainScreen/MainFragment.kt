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
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.database.firebase.*
import com.mudryakov.collectivenote.databinding.FragmentMainBinding
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.*


@Suppress("DEPRECATION")
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
         if (!INTERNET) {
           noInternetAction()
        } else {
            initialization()
            restartIfInternetChanged()
        }
    }

    private fun noInternetAction() {
        checkInternetConnection({ noInternetMode() }) {
            if (REPOSITORY is AppRoomRepository)
                restartActivity()
             else {
                INTERNET = true
                initialization()
            }
        }
    }

    private fun noInternetMode() {
        INTERNET = false
        REPOSITORY = ROOM_REPOSITORY
        initialization()
        initNoInternetBtn()
    }


    private fun initCurrency() {
        if (AppPreference.getCurrency() == FAIL) {
            REF_DATABASE_ROOT.child(NODE_ROOM_DATA).child(CURRENT_ROOM_UID).child(
                CHILD_ROOM_CURRENCY
            ).addMySingleListener {
                val currentCurrency = it.value.toString()
                AppPreference.setCurrency(currentCurrency)
                ROOM_CURRENCY = currentCurrency
            }
        } else ROOM_CURRENCY = AppPreference.getCurrency()
    }

    private fun initDrawer() {
        APP_ACTIVITY.actionBar?.setDisplayHomeAsUpEnabled(true)
        APP_ACTIVITY.actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        APP_ACTIVITY.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun initObservers() {
        mObserver = Observer { list ->
            var totalSum = "0.00"
            list.forEach {
                totalSum = calculateSum(totalSum, it.totalPayAtCurrentRoom)
                mAdapter?.addItem(it)
            }
            mBinding.loadingLayout.visibility = View.GONE
            mBinding.mainFragmentTotalPaymentRoom.text =
                getString(R.string.total_sum_payed, totalSum, ROOM_CURRENCY)
            APP_ACTIVITY.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
        mViewModel.allMembers.observe(this, mObserver)
    }

    private fun initialization() {
        APP_ACTIVITY.back = false
        APP_ACTIVITY.title = APP_ACTIVITY.getString(R.string.app_name)
        CURRENT_ROOM_UID = AppPreference.getRoomId()
        mViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        initDrawer()
        initFabNewPayment()
        initRecycle()
        initObservers()
        initCurrency()
    }

    private fun initFabNewPayment() {
        if (INTERNET) {
            mBinding.mainAddNewPayment.makeVisible()
            mBinding.mainAddNewPayment.setOnClickListener {
                fastNavigate(R.id.action_mainFragment_to_newPaymentFragment)
            }
        } else mBinding.mainAddNewPayment.makeGone()
    }

    private fun initRecycle() {
        mRecycle = mBinding.fragmentMainRecycle
        mAdapter = MainRecycleAdapter()
        mLayoutManager = LinearLayoutManager(this.context)
        mRecycle.adapter = mAdapter
    }

    override fun onDestroyView() {
        mAdapter = null
        _Binding = null
        mViewModel.allMembers.removeObserver(mObserver)
        APP_ACTIVITY.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        restartIfInternetChanged()
        super.onDestroyView()
    }

}
