package com.mudryakov.collectivenote.screens.singleUserPayments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.ROOM_CURRENCY
import com.mudryakov.collectivenote.utilits.setImage
import com.mudryakov.collectivenote.utilits.transformToDate
import kotlinx.android.synthetic.main.single_user_payments_recycle_item.view.*

class SinglePaymentAdapter : RecyclerView.Adapter<SinglePaymentAdapter.myViewHolder>() {
    val listOfpayments = mutableListOf<PaymentModel>()

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sum = view.single_payment_summ
        val description = view.single_payment_description
        val date = view.single_payment_date
        val image = view.single_pay_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_user_payments_recycle_item, parent, false)
        return myViewHolder((view))
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val curPayment = listOfpayments[position]
        holder.date.text = curPayment.time.toString().transformToDate()
        holder.description.text = curPayment.description
        holder.sum.text = APP_ACTIVITY.getString(
            R.string.sum_currency, curPayment.summ,
            ROOM_CURRENCY
        )
        if (curPayment.imageUrl != "empty") {
            holder.image.visibility = View.VISIBLE
            holder.image.setImage(curPayment.imageUrl)
        } else holder.image.visibility = View.GONE

    }

    override fun getItemCount(): Int = listOfpayments.size

    fun addItem1(payment: PaymentModel) {
        if (!listOfpayments.contains(payment)) {
            listOfpayments.add(payment)
            listOfpayments.sortByDescending { it.time.toString() }
            notifyItemInserted(listOfpayments.indexOf(payment))
        }
    }
}