package com.mudryakov.collectivenote.screens.singleUserPayments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.databinding.FragmentSingleUserPaymentsBinding
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY

class SingleUserPayments : Fragment() {
    var _Biding: FragmentSingleUserPaymentsBinding? = null
    val mBinding get() = _Biding!!
    var mAdapter: SinglePaymentAdapter? = null
    lateinit var mViewModel: SingleUserPaymentViewModel
    lateinit var mRecycle: RecyclerView
    var mPaymentObserver: Observer<List<PaymentModel>>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Biding = FragmentSingleUserPaymentsBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialization()

    }

    private fun initialization() {
        mViewModel = ViewModelProvider(this).get(SingleUserPaymentViewModel::class.java)
        mAdapter = SinglePaymentAdapter()
        mRecycle = mBinding.singlePaymentsAdapter
        mRecycle.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        mRecycle.adapter = mAdapter
        mPaymentObserver= Observer {
            mViewModel.singleUserPayments.value?.forEach{
                mAdapter?.addItem(it)
            }
        }
    }

    override fun onDestroyView() {
        _Biding = null
        mAdapter = null
        mPaymentObserver = null
        super.onDestroyView()
    }

}
