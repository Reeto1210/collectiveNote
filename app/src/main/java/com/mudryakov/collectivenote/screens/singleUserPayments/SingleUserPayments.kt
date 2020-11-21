package com.mudryakov.collectivenote.screens.singleUserPayments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.databinding.SinglePaymentFragmentBinding
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY

class SingleUserPayments : Fragment() {
    private var _Binding: SinglePaymentFragmentBinding? = null
    private val mBinding get() = _Binding!!
    var mAdapter: SinglePaymentAdapter? = null
    lateinit var mViewModel: SingleUserPaymentViewModel
    lateinit var mRecycle: RecyclerView
    lateinit var mPaymentObserver: Observer<List<PaymentModel>>
    lateinit var mProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Binding = SinglePaymentFragmentBinding.inflate(layoutInflater)
        return mBinding.root
    }


    override fun onStart() {
        super.onStart()
        initialization()
    }


    private fun initialization() {
        val userId = arguments?.get("userId").toString()
        val userName = arguments?.get("userName").toString()
        mProgressBar = mBinding.singlePaymentProgressBar
        APP_ACTIVITY.title = userName
        mViewModel = ViewModelProvider(this).get(SingleUserPaymentViewModel::class.java)
        mAdapter = SinglePaymentAdapter()
        mRecycle = mBinding.singlePaymentsRecycle
        mRecycle.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        mRecycle.adapter = mAdapter
        mPaymentObserver = Observer {
            it.forEach { payment ->
                if (payment.fromId == userId) mAdapter?.addItem1(payment)
                mProgressBar.visibility = View.GONE
                mRecycle.smoothScrollToPosition(0)
            }

        }
        mViewModel.singleUserPayments.observe(this, mPaymentObserver)
    }

    override fun onDestroyView() {
        _Binding = null
        mAdapter = null
        mViewModel.singleUserPayments.removeObserver { mPaymentObserver }
        super.onDestroyView()
    }

}
