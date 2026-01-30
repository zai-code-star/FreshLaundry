package com.example.freshlaundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.R
import com.example.freshlaundry.model.Pesanan
import com.google.firebase.database.FirebaseDatabase

class PesananAdapter(private val list: List<Pesanan>) : RecyclerView.Adapter<PesananAdapter.PesananViewHolder>() {

    inner class PesananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvLayanan: TextView = itemView.findViewById(R.id.tvLayanan)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvJam: TextView = itemView.findViewById(R.id.tvJam)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasi)
        val tvCatatan: TextView = itemView.findViewById(R.id.tvCatatan)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvBerat: TextView = itemView.findViewById(R.id.tvBerat)
        val tvBiaya: TextView = itemView.findViewById(R.id.tvBiaya)
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val btnHapus: View = itemView.findViewById(R.id.btnHapusPesanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pesanan, parent, false)
        return PesananViewHolder(view)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = list[position]
        holder.tvNama.text = "Nama: ${pesanan.nama}"
        holder.tvLayanan.text = "Layanan: ${pesanan.layanan}"
        holder.tvTanggal.text = "Tanggal: ${pesanan.tanggal}"
        holder.tvJam.text = "Jam: ${pesanan.jam}"
        holder.tvLokasi.text = "Lokasi: ${pesanan.lokasi}"
        holder.tvCatatan.text = "Catatan: ${pesanan.catatan}"
        holder.tvOrderId.text = "Order ID: ${pesanan.orderId}"

        val status = if (pesanan.status.isEmpty()) "Menunggu Konfirmasi" else pesanan.status
        val berat = if (pesanan.berat.isEmpty()) "Belum ditimbang" else pesanan.berat
        val biaya = if (pesanan.biaya.isEmpty()) "Belum dihitung" else pesanan.biaya

        holder.tvStatus.text = "Status: $status"
        holder.tvBerat.text = "Berat: $berat Kg"
        holder.tvBiaya.text = "Biaya: Rp$biaya"

        // ✅ Tampilkan tombol hapus hanya jika belum diupdate admin
        val bisaDihapus =
            pesanan.status.isEmpty() && pesanan.berat.isEmpty() && pesanan.biaya.isEmpty()
        holder.btnHapus.visibility = if (bisaDihapus) View.VISIBLE else View.GONE

        holder.btnHapus.setOnClickListener {
            val context = holder.itemView.context
            val databaseRef = FirebaseDatabase.getInstance().getReference("Orders")
            pesanan.key?.let { key ->
                databaseRef.child(pesanan.tanggal)
                    .child(pesanan.jam)
                    .child(key)
                    .removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Pesanan berhasil dihapus", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Gagal menghapus pesanan", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            } ?: Toast.makeText(context, "ID pesanan tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

        override fun getItemCount(): Int = list.size
}
