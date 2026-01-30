package com.example.freshlaundry.admin.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.freshlaundry.admin.R

class LaporanFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var tvTotalOrder: TextView
    private lateinit var tvTotalPendapatan: TextView
    private lateinit var tvLayananCuciSetrika: TextView
    private lateinit var tvLayananSetrika: TextView
    private lateinit var tvLayananCuciKering: TextView
    private lateinit var tvLayananCuciBasah: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporan, container, false)

        tvTotalOrder = view.findViewById(R.id.tvTotalOrder)
        tvTotalPendapatan = view.findViewById(R.id.tvTotalPendapatan)
        tvLayananCuciSetrika = view.findViewById(R.id.tvCuciSetrika)
        tvLayananSetrika = view.findViewById(R.id.tvSetrika)
        tvLayananCuciKering = view.findViewById(R.id.tvCuciKering)
        tvLayananCuciBasah = view.findViewById(R.id.tvCuciBasah)

        database = FirebaseDatabase.getInstance().getReference("Orders")

        loadLaporanHariIni() // Bisa ganti ke loadLaporanBulanIni() kalau mau bulanan

        return view
    }

    private fun loadLaporanHariIni() {
        val hariIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var totalOrder = 0
        var totalPendapatan = 0
        val layananCount = mutableMapOf(
            "Cuci Setrika" to 0,
            "Setrika" to 0,
            "Cuci Kering" to 0,
            "Cuci Basah" to 0
        )

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val tanggal = orderSnapshot.key ?: continue

                    if (tanggal == hariIni) {
                        for (jamSnapshot in orderSnapshot.children) {
                            for (orderDetailSnapshot in jamSnapshot.children) {
                                val layanan = orderDetailSnapshot.child("layanan").getValue(String::class.java) ?: ""
                                val biaya = orderDetailSnapshot.child("biaya").getValue(String::class.java)?.toIntOrNull() ?: 0

                                layananCount[layanan] = layananCount.getOrDefault(layanan, 0) + 1
                                totalOrder++
                                totalPendapatan += biaya
                            }
                        }
                    }
                }


                tvTotalOrder.text = "Total Order: $totalOrder"
                tvTotalPendapatan.text = "Total Pendapatan: Rp$totalPendapatan"
                tvLayananCuciSetrika.text = "Cuci Setrika: ${layananCount["Cuci Setrika"]}"
                tvLayananSetrika.text = "Setrika: ${layananCount["Setrika"]}"
                tvLayananCuciKering.text = "Cuci Kering: ${layananCount["Cuci Kering"]}"
                tvLayananCuciBasah.text = "Cuci Basah: ${layananCount["Cuci Basah"]}"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
