package com.mudryakov.collectivenote.screens.mainScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentMainBinding
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.*
import javax.inject.Inject


@Suppress("DEPRECATION")
class MainFragment : Fragment() {
    var _Binding: FragmentMainBinding? = null
    val mBinding get() = _Binding!!

    @Inject
    lateinit var mViewModel: MainFragmentViewModel
    @Inject
    lateinit var mAdapter: MainRecycleAdapter
    lateinit var allGroupMembers: LiveData<List<UserModel>>
    private lateinit var mObserver: Observer<List<UserModel>>
    private lateinit var mRecycle: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()

        if (!APP_ACTIVITY.internet) {
            noInternetAction()
        } else {
            initialization()
            restartIfInternetChanged()
        }
    }

    private fun noInternetAction() {
        checkInternetConnection({ noInternetMode() }) {
               APP_ACTIVITY.internet = true
                initialization()
             }
    }

    private fun noInternetMode() {
        APP_ACTIVITY.internet = false
        initialization()
        initNoInternetBtn()
    }


    private fun initCurrency() {
        mViewModel.getUpdateCurrency()
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
                totalSum = calculateSum(totalSum, it.totalPayAtCurrentGroup)
                mAdapter.addItem(it)
            }
            mBinding.loadingLayout.visibility = View.GONE
            mBinding.mainFragmentTotalPaymentRoom.text =
                getString(R.string.total_sum_payed, totalSum, mViewModel.getCurrency())
            APP_ACTIVITY.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            DRAWER_ENABLED = true
        }
        allGroupMembers = mViewModel.allMembers()
        allGroupMembers.observe(this, mObserver)
    }

    private fun initialization() {
        APP_ACTIVITY.back = false
        setTitle(R.string.app_name)
        mViewModel.updateGroupId()
        initDrawer()
        initFabNewPayment()
        initRecycle()
        initObservers()
        initCurrency()
    }

    private fun initFabNewPayment() {
        if (APP_ACTIVITY.internet) {
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
        _Binding = null
        allGroupMembers.removeObserver(mObserver)
        APP_ACTIVITY.mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
    }
}
