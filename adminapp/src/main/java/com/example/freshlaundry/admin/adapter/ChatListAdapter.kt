package com.example.freshlaundry.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.admin.R
import com.example.freshlaundry.admin.model.User

class ChatListAdapter(
    private val users: List<User>,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUser = view.findViewById<TextView>(R.id.txtUser)
        val txtLastMessage = view.findViewById<TextView>(R.id.txtLastMessage)

        fun bind(user: User) {
            txtUser.text = user.email.substringBefore("@")
            txtLastMessage.text = user.lastChat // tambahkan ini
            itemView.setOnClickListener { onClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_user, parent, false))

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }
}
