package com.mudryakov.collectivenote.screens.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.getImage
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

        holder.fromName.text = listOfpayments[position].fromName
        holder.description.text = listOfpayments[position].description
        holder.sum.text = listOfpayments[position].summ
        holder.date.text = listOfpayments[position].time.toString().transformToDate()

        if (arOfOpened.contains(position)) holder.fullScreenImage.visibility = View.VISIBLE
        else holder.fullScreenImage.visibility = View.GONE
        if (listOfpayments[position].imageUrl != "empty"){
            holder.attachedImage.setImageResource(R.drawable.ic_photo_coloring)
          holder.fullScreenImage.setImageResource(R.drawable.test)
        }

        else {
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
          holder.attachedImage.setOnClickListener {
          if (holder.fullScreenImage.visibility == View.GONE && listOfpayments[holder.adapterPosition].imageUrl != "empty") {
           arOfOpened.add(holder.adapterPosition)
            holder.fullScreenImage.visibility = View.VISIBLE
           getImage(listOfpayments[holder.adapterPosition].imageUrl)

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





