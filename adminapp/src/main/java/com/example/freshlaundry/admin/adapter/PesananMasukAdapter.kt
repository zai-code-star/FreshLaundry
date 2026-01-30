package com.example.freshlaundry.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.admin.R
import com.example.freshlaundry.admin.model.pesanananmasuk

class PesananMasukAdapter(
    private val listPesanan: List<pesanananmasuk>,
    private val onItemClick: (pesanananmasuk) -> Unit,
    private val onStartPickupClick: (pesanananmasuk) -> Unit,
    private val showPickupButton: Boolean = false
) : RecyclerView.Adapter<PesananMasukAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvLayanan: TextView = itemView.findViewById(R.id.tvLayanan)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasi)
        val tvJam: TextView = itemView.findViewById(R.id.tvJam)
        val tvCatatan: TextView = itemView.findViewById(R.id.tvCatatan)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvBerat: TextView = itemView.findViewById(R.id.tvBerat)
        val tvBiaya: TextView = itemView.findViewById(R.id.tvBiaya)
        val btnPickup: Button = itemView.findViewById(R.id.btnMulaiPenjemputan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pesanan_masuk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pesanan = listPesanan[position]
        holder.tvOrderId.text = "Order ID: ${pesanan.orderId}"
        holder.tvNama.text = "Nama: ${pesanan.nama}"
        holder.tvLayanan.text = "Layanan: ${pesanan.layanan}"
        holder.tvLokasi.text = "Lokasi: ${pesanan.lokasi}"
        holder.tvJam.text = "Jam: ${pesanan.jam}"
        holder.tvCatatan.text = "Catatan: ${pesanan.catatan}"
        holder.tvTanggal.text = "Tanggal: ${pesanan.tanggal}"
        holder.tvStatus.text = "Status: ${pesanan.status}"
        holder.tvBerat.text = "Berat: ${pesanan.berat} Kg"
        holder.tvBiaya.text = "Biaya: Rp${pesanan.biaya}"

        holder.itemView.setOnClickListener {
            onItemClick(pesanan)
        }

        // Tampilkan tombol pickup jika diperlukan
        if (showPickupButton) {
            holder.btnPickup.visibility = View.VISIBLE
            holder.btnPickup.setOnClickListener {
                onStartPickupClick(pesanan)
            }
        } else {
            holder.btnPickup.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = listPesanan.size
}
