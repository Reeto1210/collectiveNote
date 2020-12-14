package com.mudryakov.collectivenote.screens.mainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import com.mudryakov.collectivenote.utility.AppPreference
import com.mudryakov.collectivenote.utility.ROOM_CURRENCY
import kotlinx.android.synthetic.main.main_fragment_recycle_item.view.*

class MainRecycleAdapter : RecyclerView.Adapter<MainRecycleAdapter.MyViewHolder>() {
    var list = mutableListOf<UserModel>()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.main_recycle_item_username
        val totalPayed: TextView = view.main_recycle_item_total_summ
        val layout: ConstraintLayout = view.recycle_main_layout
        val divider: View = view.divider
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_fragment_recycle_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentUser = list[position]
        holder.divider.visibility = if (position == 0) View.GONE else View.VISIBLE
        holder.totalPayed.text =
            if (currentUser.totalPayAtCurrentGroup.isNotEmpty()) APP_ACTIVITY.getString(
                R.string.sum_currency, currentUser.totalPayAtCurrentGroup,
                ROOM_CURRENCY
            ) else "0"
        holder.username.text = currentUser.name
        setAnimation(holder.itemView)
    }

    override fun getItemCount() = list.size

    fun addItem(user: UserModel) =
        if (!list.contains(user)) {
            addUserToRecycle(user)
            notifyItemInserted(list.indexOf(user))
        } else {
            list.forEachIndexed { i: Int, userModel: UserModel ->
                if (userModel.firebaseId == user.firebaseId && userModel.totalPayAtCurrentGroup != user.totalPayAtCurrentGroup) {
                    userModel.totalPayAtCurrentGroup = user.totalPayAtCurrentGroup
                    notifyItemChanged(i)
                }
            }

        }

    private fun addUserToRecycle(user: UserModel) {
        list.add(user)
        if (user.firebaseId == CURRENT_UID) {
            list.last().name = AppPreference.getUserName()
            list.reverse()
        }
        notifyItemInserted(list.indexOf(user))
    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        val curUser = list[holder.adapterPosition]
        holder.layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userId", curUser.firebaseId)
            bundle.putString("userName", curUser.name)
            APP_ACTIVITY.navController.navigate(
                R.id.action_mainFragment_to_singleUserPayments,
                bundle
            )
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        holder.layout.setOnClickListener { }
        super.onViewDetachedFromWindow(holder)
    }

    private fun setAnimation(viewToAnimate: View) {
        val animation: Animation =
            AnimationUtils.loadAnimation(APP_ACTIVITY, android.R.anim.slide_in_left)
        viewToAnimate.startAnimation(animation)
    }
}