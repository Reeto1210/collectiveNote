package com.mudryakov.collectivenote.screens.singleUserPayments

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utility.*
import kotlinx.android.synthetic.main.single_user_payments_recycle_item.view.*

class SinglePaymentAdapter : RecyclerView.Adapter<SinglePaymentAdapter.MyViewHolder>() {
    val listOfpayments = mutableListOf<PaymentModel>()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sum: TextView = view.single_payment_summ
        val description: TextView = view.single_payment_description
        val date: TextView = view.single_payment_date
        val image: ImageView = view.single_pay_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_user_payments_recycle_item, parent, false)
        return MyViewHolder((view))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
        setAnimation(holder.itemView)
    }

    override fun getItemCount(): Int = listOfpayments.size

    fun addItem(payment: PaymentModel) {
        if (!listOfpayments.contains(payment)) {
            listOfpayments.add(payment)
            listOfpayments.sortByDescending { it.time.toString() }
            notifyItemInserted(listOfpayments.indexOf(payment))
        }
    }

    fun deleteItem(position: Int) {
        notifyItemRemoved(position)
        listOfpayments.removeAt(position)
    }

    private fun setAnimation(viewToAnimate: View) {
        val animation: Animation =
            AnimationUtils.loadAnimation(APP_ACTIVITY, R.anim.fast_slide_left)
        viewToAnimate.startAnimation(animation)
    }


}