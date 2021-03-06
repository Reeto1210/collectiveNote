package com.mudryakov.collectivenote.screens.singleUserPayments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CHILD_FIREBASE_ID
import com.mudryakov.collectivenote.database.firebase.CHILD_NAME
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.databinding.SinglePaymentFragmentBinding
import com.mudryakov.collectivenote.di.SinglePaymentSubComponent
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.screens.BaseFragmentBack
import com.mudryakov.collectivenote.utility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class SingleUserPaymentsFragment : BaseFragmentBack() {
    private var _Binding: SinglePaymentFragmentBinding? = null
    private val mBinding get() = _Binding!!

    @Inject
    lateinit var mAdapter: SinglePaymentAdapter
    @Inject
    lateinit var mViewModel: SingleUserPaymentViewModel
    lateinit var singlePaymentComponent:SinglePaymentSubComponent
    lateinit var mRecycle: RecyclerView
    lateinit var mPaymentObserver: Observer<List<PaymentModel>>
    var userId = ""
    var isAvailable = false

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
          mPaymentObserver = Observer {
            var isEmpty = true
            it.forEach { payment ->
                if (payment.fromId == userId) {
                    mAdapter.addItem(payment)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecycle() {
        mRecycle = mBinding.singlePaymentsRecycle
        mRecycle.layoutManager = LinearLayoutManager(APP_ACTIVITY)
        mRecycle.adapter = mAdapter
        if (userId == CURRENT_UID) {
            initDeleteFunction()
        }
    }


    private fun initDeleteFunction() {
       if (APP_ACTIVITY.internet) setHasOptionsMenu(true)
        val icon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.delete)
        val p = Paint()
        val mItemTouchHelper = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {


            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,

                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val iconRectF = calculateRect(viewHolder, dX)
                    c.drawBitmap(icon, null, iconRectF, p)

                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }


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
                    mAdapter.listOfpayments[position]
                val dialogBuilder = AlertDialog.Builder(APP_ACTIVITY)
                dialogBuilder
                    .setTitle(getString(R.string.confirm_delete))
                    .setMessage(
                        getString(
                            R.string.alert_dialog_message_delete,
                            currentPayment.time.toString().transformToDate()
                        )
                    )
                    .setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                        mAdapter.deleteItem(position)
                              deletePaymentFromFirebase(currentPayment)

                    }
                    .setNegativeButton(getString(R.string.no)) { _: DialogInterface, _: Int ->
                        mAdapter.notifyItemChanged(position)
                    }
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setOnCancelListener { mAdapter.notifyItemChanged(position) }
                    .show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(mItemTouchHelper)
        if (APP_ACTIVITY.internet) itemTouchHelper.attachToRecyclerView(mRecycle)
    }


    private fun initialization() {
        userId = arguments?.get(BUNDLE_ID).toString()
        val userName = arguments?.get(BUNDLE_NAME).toString()
        APP_ACTIVITY.title = userName
    }

    override fun onDestroyView() {
        _Binding = null

        mViewModel.singleUserPayments.removeObserver { mPaymentObserver }
        super.onDestroyView()
    }


    private fun deletePaymentFromFirebase(payment: PaymentModel) {
        isAvailable = false
        checkInternetConnection({
            restartActivity()
        }) {

            mViewModel.deletePayment(payment) {
                showToast(R.string.payment_has_been_deleted_toast)
                CoroutineScope(IO).launch {
                    delay(200)
                    isAvailable = true
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.single_payments_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_payment -> {
                isAvailable = true
                showToast(R.string.toast_at_delete_menu)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
singlePaymentComponent = MyApplication.appComponent.getSinglePaymentSub().create()
  singlePaymentComponent.inject(this)

    }

}
