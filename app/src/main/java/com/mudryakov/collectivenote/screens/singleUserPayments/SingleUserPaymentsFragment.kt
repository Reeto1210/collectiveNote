package com.mudryakov.collectivenote.screens.singleUserPayments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.databinding.SinglePaymentFragmentBinding
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SingleUserPaymentsFragment : BaseFragmentBack() {
    private var _Binding: SinglePaymentFragmentBinding? = null
    private val mBinding get() = _Binding!!
    var mAdapter: SinglePaymentAdapter? = null
    lateinit var mViewModel: SingleUserPaymentViewModel
    lateinit var mRecycle: RecyclerView
    lateinit var mPaymentObserver: Observer<List<PaymentModel>>
    var userId = ""
    var isAvailable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _Binding = SinglePaymentFragmentBinding.inflate(layoutInflater)
        return mBinding.root
    }


    override fun onStart() {
        super.onStart()
        initialization()
        initRecycle()
        initObserver()
    }

    private fun initObserver() {
              mViewModel = ViewModelProvider(this).get(SingleUserPaymentViewModel::class.java)
        mPaymentObserver = Observer {
            var isEmpty = true
            it.forEach { payment ->
                if (payment.fromId == userId) {
                    mAdapter?.addItem(payment)
                    isEmpty = false

                }
                mRecycle.smoothScrollToPosition(0)
            }



            mBinding.singlePaymentProgressBar.makeInvisible()
            if (isEmpty) mBinding.singlePaymentsEmpty.makeVisible()
            else mBinding.singlePaymentsEmpty.makeGone()
        }
        mViewModel.singleUserPayments.observe(this, mPaymentObserver)
    }

    private fun initRecycle() {
        mAdapter = SinglePaymentAdapter()
        mRecycle = mBinding.singlePaymentsRecycle
        mRecycle.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        mRecycle.adapter = mAdapter
        if (userId == CURRENT_UID) {

            val mItemTouchHelper = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                override fun isItemViewSwipeEnabled(): Boolean {
                    return isAvailable
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val currentPayment =
                        mAdapter?.listOfpayments?.get(position)
                    val dialogBuilder = AlertDialog.Builder(APP_ACTIVITY)
                    dialogBuilder
                        .setTitle(getString(R.string.confirm_delete))
                        .setMessage(
                            getString(
                                R.string.alert_dialog_message_delete,
                                currentPayment?.time?.toString()?.transformToDate()
                            )
                        )
                        .setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                            mAdapter?.deleteItem(position)
                            if (currentPayment != null) {
                                deletePaymentFromFirebase(currentPayment)
                            }
                        }
                        .setNegativeButton(getString(R.string.no)) { _: DialogInterface, _: Int ->
                            mAdapter?.notifyItemChanged(position)
                        }
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setOnCancelListener { mAdapter?.notifyItemChanged(position) }
                        .show()
                }
            }
            val itemTouchHelper = ItemTouchHelper(mItemTouchHelper)
            if (INTERNET) itemTouchHelper.attachToRecyclerView(mRecycle)
        }
    }


    private fun initialization() {
        userId = arguments?.get("userId").toString()
        val userName = arguments?.get("userName").toString()
        APP_ACTIVITY.title = userName
    }

    override fun onDestroyView() {
        _Binding = null
        mAdapter = null
        mViewModel.singleUserPayments.removeObserver { mPaymentObserver }
        super.onDestroyView()
    }


    private fun deletePaymentFromFirebase(payment: PaymentModel) {
        isAvailable = false
        checkInternetConnection({
            restartActivity()
        }) {

            mViewModel.deletePayment(payment) {
                showToast(getString(R.string.payment_has_been_deleted_toast))
                CoroutineScope(IO).launch {
                    delay(200)
                    isAvailable = true
                }
            }
        }
    }
}
