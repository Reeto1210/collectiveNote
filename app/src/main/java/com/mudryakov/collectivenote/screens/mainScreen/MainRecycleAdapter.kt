package com.mudryakov.collectivenote.screens.mainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import kotlinx.android.synthetic.main.main_fragment_recycle_item.view.*

class MainRecycleAdapter : RecyclerView.Adapter<MainRecycleAdapter.myViewHolder>() {
    val list = mutableListOf<UserModel>()

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username = view.main_recycle_item_username
        val totalPayd = view.main_recycle_item_total_summ
        val layout = view.recycle_main_layout
        val divider = view.divider
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_fragment_recycle_item, parent, false)
               return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentUser = list[position]
         holder.divider.visibility = if (position == 0) View.GONE else View.VISIBLE
        holder.totalPayd.text =
            if (currentUser.totalPayAtCurrentRoom.isNotEmpty()) APP_ACTIVITY.getString(R.string.sum_currency, currentUser.totalPayAtCurrentRoom) else "0"
        holder.username.text = currentUser.name

    }

    override fun getItemCount() = list.size

    fun addItem(user: UserModel) {
        var iteral = -1
        if (!list.contains(user))
            addUserToRecycle(user)
        else {
            list.forEach {
                if (it.firebaseId == user.firebaseId && it.totalPayAtCurrentRoom != user.totalPayAtCurrentRoom)
                    iteral = list.indexOf(it)
            }
            if (iteral != -1) {
                list.removeAt(iteral)
                notifyItemRemoved(iteral)
                addUserToRecycle(user)
            }
        }
    }

    private fun addUserToRecycle(user: UserModel) {
        list.add(user)
        if (user.firebaseId == CURRENT_UID) {
            list.reverse()
        }
        notifyItemInserted(list.indexOf(user))
    }

    override fun onViewAttachedToWindow(holder: myViewHolder) {
        val curUser = list[holder.adapterPosition]
        holder.layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userId", curUser.firebaseId)
            bundle.putString("userName", curUser.name)
            APP_ACTIVITY.navConroller.navigate(
                R.id.action_mainFragment_to_singleUserPayments,
                bundle
            )

        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: myViewHolder) {
        holder.layout.setOnClickListener { }
        super.onViewDetachedFromWindow(holder)
    }
}