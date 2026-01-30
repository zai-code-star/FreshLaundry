package com.example.freshlaundry.adapter

// Adapter
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.R
import com.example.freshlaundry.model.Message
import java.util.ArrayList

class MessageAdapter(
    private val context: Context,
    private val messageList: ArrayList<Message>,
    private val currentUserId: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false)
            SentViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_received, parent, false)
            ReceivedViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        val formattedTime = DateFormat.format("HH:mm", message.timestamp).toString()
        if (holder is SentViewHolder) {
            holder.sentText.text = message.message
            holder.sentTime.text = formattedTime
        } else if (holder is ReceivedViewHolder) {
            holder.receivedText.text = message.message
            holder.receivedTime.text = formattedTime
        }
    }

    override fun getItemCount(): Int = messageList.size

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentText: TextView = itemView.findViewById(R.id.sentMessage)
        val sentTime: TextView = itemView.findViewById(R.id.sentTime)
    }

    inner class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedText: TextView = itemView.findViewById(R.id.receivedMessage)
        val receivedTime: TextView = itemView.findViewById(R.id.receivedTime)
    }
}