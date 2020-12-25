package com.mudryakov.collectivenote.screens.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utility.*
import kotlinx.android.synthetic.main.history_recycle_item.view.*

class HistoryRecyclerAdapter : RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder>() {
    var arOfOpened = arrayListOf<Int>()
    var listOfPayments = mutableListOf<PaymentModel>()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fromName: TextView = view.history_from
        val description: TextView = view.history_description
        val date: TextView = view.history_date
        val sum: TextView = view.history_summ
        val attachedImage: ImageView = view.history_image
        val fullScreenImage: ImageView = view.history_full_image
        val baseLayout: ConstraintLayout = view.history_recycle_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_recycle_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val curPayment = listOfPayments[position]
        holder.fromName.text = curPayment.fromName
        holder.description.text = curPayment.description
        holder.sum.text = APP_ACTIVITY.getString(
            R.string.sum_currency, curPayment.summ,
            ROOM_CURRENCY
        )
        holder.date.text = curPayment.time.toString().transformToDate()

        if (arOfOpened.contains(position)) {
            holder.fullScreenImage.makeVisible()
            holder.fullScreenImage.setImage(curPayment.imageUrl)
        } else holder.fullScreenImage.makeGone()
        if (curPayment.imageUrl != EMPTY) {
            holder.attachedImage.setImageResource(R.drawable.ic_photo_coloring)
        } else {
            holder.attachedImage.setImageResource(R.drawable.ic_photo)
        }


    }

    override fun getItemCount(): Int = listOfPayments.size

    fun addItem(payment: PaymentModel) {
        if (!listOfPayments.contains(payment)) {
            listOfPayments.add(payment)
            listOfPayments.sortByDescending { it.time.toString() }
            notifyItemInserted(listOfPayments.indexOf(payment))
        }
    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        val curPayment = listOfPayments[holder.adapterPosition]
        if (curPayment.imageUrl != EMPTY)
            holder.baseLayout.setOnClickListener {
                if (holder.fullScreenImage.visibility == View.GONE) {
                    arOfOpened.add(holder.adapterPosition)
                    holder.fullScreenImage.setImage(curPayment.imageUrl)
                } else {
                    arOfOpened.remove(holder.adapterPosition)
                }
                notifyItemChanged(holder.adapterPosition)
                       }
                 setAnimation(holder.itemView)
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        holder.baseLayout.setOnClickListener {}
        super.onViewDetachedFromWindow(holder)
    }

}

private fun setAnimation(viewToAnimate: View) {
    val animation: Animation =
        AnimationUtils.loadAnimation(APP_ACTIVITY, R.anim.fast_slide_left)
    viewToAnimate.startAnimation(animation)
}



