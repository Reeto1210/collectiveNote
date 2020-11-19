package com.mudryakov.collectivenote.screens.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.setImage
import com.mudryakov.collectivenote.utilits.transformToDate
import kotlinx.android.synthetic.main.history_recycle_item.view.*

class HistoryRecyclerAdapter() : RecyclerView.Adapter<HistoryRecyclerAdapter.myViewHolder>() {
    var arOfOpened = arrayListOf<Int>()
    var listOfpayments = mutableListOf<PaymentModel>()

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fromName: TextView = view.history_from
        val description: TextView = view.history_description
        val date: TextView = view.history_date
        val sum: TextView = view.history_summ
        val attachedImage: ImageView = view.history_image
        val fullScreenImage: ImageView = view.history_full_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_recycle_item, parent, false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val curPayment = listOfpayments[position]
        holder.fromName.text = curPayment.fromName
        holder.description.text = curPayment.description
        holder.sum.text = curPayment.summ
        holder.date.text = curPayment.time.toString().transformToDate()

        if (arOfOpened.contains(position)) holder.fullScreenImage.visibility = View.VISIBLE
        else holder.fullScreenImage.visibility = View.GONE
        if (curPayment.imageUrl != "empty") {
            holder.attachedImage.setImageResource(R.drawable.ic_photo_coloring)
        } else {
            holder.attachedImage.setImageResource(R.drawable.ic_photo)


        }
    }

    override fun getItemCount(): Int = listOfpayments.size

    fun addItem(payment: PaymentModel) {
        if (!listOfpayments.contains(payment)) {
            listOfpayments.add(payment)
            listOfpayments.sortByDescending { it.time.toString() }
            notifyItemInserted(listOfpayments.indexOf(payment))
        }
    }

    override fun onViewAttachedToWindow(holder: myViewHolder) {
        val curPayment = listOfpayments[holder.adapterPosition]
        holder.attachedImage.setOnClickListener {
            if (holder.fullScreenImage.visibility == View.GONE && curPayment.imageUrl != "empty") {
                holder.fullScreenImage.visibility = View.VISIBLE
                if (holder.adapterPosition != listOfpayments.lastIndex) {
                    arOfOpened.add(holder.adapterPosition)
                }
                holder.fullScreenImage.setImage(curPayment.imageUrl)
            } else {
                holder.fullScreenImage.visibility = View.GONE
                arOfOpened.remove(holder.adapterPosition)

            }
        }
        holder.fullScreenImage.setOnClickListener {
            holder.fullScreenImage.visibility = View.GONE
            arOfOpened.remove(holder.adapterPosition)
        }
    }

    override fun onViewDetachedFromWindow(holder: myViewHolder) {

        holder.attachedImage.setOnClickListener {}
        super.onViewDetachedFromWindow(holder)
    }

}





