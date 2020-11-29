package com.mudryakov.collectivenote.screens.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.makeGone
import com.mudryakov.collectivenote.utilits.makeVisible
import com.mudryakov.collectivenote.utilits.setImage
import com.mudryakov.collectivenote.utilits.transformToDate
import kotlinx.android.synthetic.main.history_recycle_item.view.*

class HistoryRecyclerAdapter() : RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder>() {
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
        holder.sum.text = curPayment.summ
        holder.date.text = curPayment.time.toString().transformToDate()

        if (arOfOpened.contains(position)) {
            holder.fullScreenImage.makeVisible()
            holder.fullScreenImage.setImage(curPayment.imageUrl)
        } else holder.fullScreenImage.makeGone()
        if (curPayment.imageUrl != "empty") {
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
        holder.baseLayout.setOnClickListener {
            if (holder.fullScreenImage.visibility == View.GONE && curPayment.imageUrl != "empty") {
                holder.fullScreenImage.makeVisible()
                if (holder.adapterPosition != listOfPayments.lastIndex) {
                    arOfOpened.add(holder.adapterPosition)
                }
                holder.fullScreenImage.setImage(curPayment.imageUrl)
            } else {
                holder.fullScreenImage.makeGone()
                arOfOpened.remove(holder.adapterPosition)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        holder.baseLayout.setOnClickListener {}
        super.onViewDetachedFromWindow(holder)
    }
}





