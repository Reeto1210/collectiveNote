package com.mudryakov.collectivenote.screens.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentHistoryBinding
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY


class HistoryFragment : Fragment() {
    var _Binding: FragmentHistoryBinding? = null
    val mBinding get() = _Binding!!
    lateinit var mViewModel: HistoryFragmentViewModel
    lateinit var mPaymentObserver: Observer<List<PaymentModel>>
    var mAdapter: HistoryRecyclerAdapter? = null
    lateinit var mRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _Binding = FragmentHistoryBinding.inflate(layoutInflater)
        APP_ACTIVITY.title = getString(R.string.history)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
        mViewModel.paymentList.observe(this, mPaymentObserver)
    }

    private fun initialization() {
       var i = 0
        mViewModel = ViewModelProvider(this).get(HistoryFragmentViewModel::class.java)
        mRecyclerView = mBinding.historyRecycle
        mAdapter = HistoryRecyclerAdapter()
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        mPaymentObserver = Observer {
            it.forEach { pay ->
                mAdapter?.addItem(pay)
           i++
           println(i.toString())
            }
            mRecyclerView.smoothScrollToPosition(0)
        }
    }

    override fun onDestroyView() {
        _Binding = null
        mViewModel.paymentList.removeObserver(mPaymentObserver)
        mAdapter = null
        super.onDestroyView()
    }
}
