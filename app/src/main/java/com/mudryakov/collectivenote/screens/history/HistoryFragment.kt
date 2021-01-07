package com.mudryakov.collectivenote.screens.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.databinding.FragmentHistoryBinding
import com.mudryakov.collectivenote.di.HistorySubCoponent
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import com.mudryakov.collectivenote.utility.makeInvisible
import com.mudryakov.collectivenote.utility.makeVisible
import javax.inject.Inject


class HistoryFragment : BaseFragmentBack() {
    var _Binding: FragmentHistoryBinding? = null
    private val mBinding get() = _Binding!!

    @Inject
    lateinit var mViewModel: HistoryFragmentViewModel

    @Inject
    lateinit var mAdapter: HistoryRecyclerAdapter

    lateinit var historyComponent: HistorySubCoponent
    lateinit var mPaymentObserver: Observer<List<PaymentModel>>
    lateinit var mRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = FragmentHistoryBinding.inflate(layoutInflater)
        APP_ACTIVITY.title = getString(R.string.history)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initialization()
        initObservers()
    }

    private fun initObservers() {
        mPaymentObserver = Observer {
            it.forEach { pay ->
                mAdapter.addItem(pay)
            }
            mRecyclerView.smoothScrollToPosition(0)
            mBinding.historyProgressBar.makeInvisible()
            if (it.isEmpty()) mBinding.historyListIsEmpty.makeVisible()
            else mBinding.historyListIsEmpty.makeInvisible()
        }
        mViewModel.paymentList.observe(this, mPaymentObserver)
    }

    private fun initialization() {
        mRecyclerView = mBinding.historyRecycle
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(APP_ACTIVITY)
    }

    override fun onDestroyView() {
        _Binding = null
        mViewModel.paymentList.removeObserver(mPaymentObserver)
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        historyComponent = MyApplication.appComponent.getHistorySub().create()
        historyComponent.inject(this)
    }
}
