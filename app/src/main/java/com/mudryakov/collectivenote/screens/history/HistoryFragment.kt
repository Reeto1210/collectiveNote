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
    lateinit var mObserver: Observer<List<PaymentModel>>
    lateinit var mAdapter: HistoryRecyclerAdapter
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
        mViewModel.paymentList.observe(this, mObserver)
    }

    private fun initialization() {
        mRecyclerView = mBinding.historyRecycle
        mAdapter = HistoryRecyclerAdapter()
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        mViewModel = ViewModelProvider(this).get(HistoryFragmentViewModel::class.java)
        mObserver = Observer {
            it.forEach { pay ->
                //тут меняем id человека на имя
                mAdapter.addItem(pay) }
            mRecyclerView.smoothScrollToPosition(0)

        }
    }


}
