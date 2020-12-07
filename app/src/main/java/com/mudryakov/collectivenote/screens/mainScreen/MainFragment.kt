package com.mudryakov.collectivenote.screens.mainScreen

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.mudryakov.collectivenote.database.firebase.*
import com.mudryakov.collectivenote.databinding.FragmentMainBinding
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.*


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
        checkInternet()
    }

    private fun checkInternet() {
        checkInternetConnection(onFail = {}) {
            if (!INTERNET) {
                restartActivity()
            }
        }
        if (INTERNET)
            checkInternetConnection({ buildMainNoInternetDialog() }) {
                initialization()
                initObservers()
                initCurrency()
            }
        else {
            noInternetMode()
        }
    }

    private fun buildMainNoInternetDialog() {
        val dialog = AlertDialog.Builder(APP_ACTIVITY)
        dialog
            .setCancelable(false)
            .setIcon(R.drawable.ic_no_internet)
            .setTitle(APP_ACTIVITY.getString(R.string.internet_alert_dialog_title))
            .setMessage(APP_ACTIVITY.getString(R.string.internet_alert_dialog_message))
            .setPositiveButton("Попробовать снова") { _: DialogInterface, _: Int -> checkInternet() }
            .setNeutralButton("Продолжить без интернета") { _: DialogInterface, _: Int ->
                run {
                    INTERNET = false
                    APP_ACTIVITY.startNoInternetAnimation()
                    noInternetMode()
                }
            }
            .show()
    }

    private fun noInternetMode() {
        APP_ACTIVITY.mBinding.noInternetIndicatorBtn.setOnClickListener {
            checkInternetConnection({ showNoInternetToast() }) {
                restartActivity()
            }
        }
        REPOSITORY = ROOM_REPOSITORY
        initialization()
        initObservers()
        initCurrency()
    }


    private fun initCurrency() {
        if (AppPreference.getCurrency() == "fail") {
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
            var totalSum = 0L
            list.forEach {
                totalSum += it.totalPayAtCurrentRoom.toLong()
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
        initDrawer()
        APP_ACTIVITY.back = false
        APP_ACTIVITY.title = APP_ACTIVITY.getString(R.string.app_name)
        CURRENT_ROOM_UID = AppPreference.getRoomId()
        mViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        initFabNewPayment()
        initRecycle()
    }

    private fun initFabNewPayment() {
        if (INTERNET) {
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
