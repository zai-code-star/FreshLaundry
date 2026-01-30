package com.example.freshlaundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.R
import com.example.freshlaundry.model.Notifikasi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifikasiAdapter(private val list: List<Notifikasi>) :
    RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPesan: TextView = view.findViewById(R.id.tvPesan)
        val tvWaktu: TextView = view.findViewById(R.id.tvWaktu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notifikasi, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = list[position]
        holder.tvPesan.text = notif.pesan
        holder.tvWaktu.text = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            .format(Date(notif.timestamp))
    }
}
