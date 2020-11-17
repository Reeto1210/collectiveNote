package com.mudryakov.collectivenote.screens.mainScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.models.UserModel
import kotlinx.android.synthetic.main.main_fragment_recycle_item.view.*

class MainRecycleAdapter : RecyclerView.Adapter<MainRecycleAdapter.myViewHolder>() {
    val list = mutableListOf<UserModel>()

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username = view.main_recycle_item_username
        val totalPayd = view.main_recycle_item_total_summ

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_fragment_recycle_item, parent, false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentUser = list[position]
        holder.totalPayd.text = if ( currentUser.totalPayAtCurrentRoom.isNotEmpty()) currentUser.totalPayAtCurrentRoom else "0"
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
       if (user.firebaseId == CURRENT_UID){
          list.reverse()
       }
        notifyItemInserted(list.indexOf(user))
    }


}