package com.example.freshlaundry.admin.tab

import android.content.Context
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshlaundry.admin.R
import com.example.freshlaundry.admin.adapter.PesananMasukAdapter
import com.example.freshlaundry.admin.helper.FcmV1Sender
import com.example.freshlaundry.admin.model.pesanananmasuk
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PesananMasukFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PesananMasukAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var spinnerFilter: Spinner
    private val listPesanan = mutableListOf<pesanananmasuk>()
    private var selectedFilter: String = "Semua"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pesanan_masuk, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPesananMasuk)
        spinnerFilter = view.findViewById(R.id.spinnerFilter)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PesananMasukAdapter(
            listPesanan,
            onItemClick = { pesanan -> showDetailDialog(pesanan) },
            onStartPickupClick = {}, // tidak digunakan
            showPickupButton = false // sembunyikan tombol pickup
        )

        recyclerView.adapter = adapter

        setupFilterSpinner()
        loadPesananDariFirebase()

        return view
    }

    private fun setupFilterSpinner() {
        val options = listOf("Semua", "Diterima", "Pesanan Baru")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = spinnerAdapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedFilter = options[position]
                loadPesananDariFirebase()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadPesananDariFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Orders")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPesanan.clear()
                for (tanggalSnapshot in snapshot.children) {
                    val tanggal = tanggalSnapshot.key ?: continue
                    for (jamSnapshot in tanggalSnapshot.children) {
                        val jam = jamSnapshot.key ?: continue
                        for (orderSnapshot in jamSnapshot.children) {
                            val pesanan = orderSnapshot.getValue(pesanananmasuk::class.java)
                            pesanan?.let {
                                val status = it.status ?: ""
                                if (selectedFilter == "Semua"
                                    || (selectedFilter == "Diterima" && status == "Diterima")
                                    || (selectedFilter == "Pesanan Baru" && status.isBlank())
                                ) {
                                    listPesanan.add(it.copy(tanggal = tanggal, jam = jam))
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showDetailDialog(pesanan: pesanananmasuk) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_detail_pesanan, null)

        val spinnerStatus = dialogView.findViewById<Spinner>(R.id.spinnerStatus)
        val inputBerat = dialogView.findViewById<EditText>(R.id.etBerat)
        val inputBiaya = dialogView.findViewById<EditText>(R.id.etBiaya)

        val statusList = resources.getStringArray(R.array.status_list)
        val adapterSpinner =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusList)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapterSpinner
        spinnerStatus.setSelection(statusList.indexOf(pesanan.status))

        inputBerat.setText(pesanan.berat)
        inputBiaya.setText(pesanan.biaya)

        AlertDialog.Builder(requireContext())
            .setTitle("Detail Pesanan")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newStatus = spinnerStatus.selectedItem.toString()
                val newBerat = inputBerat.text.toString()
                val newBiaya = inputBiaya.text.toString()
                updatePesananKeFirebase(pesanan, newStatus, newBerat, newBiaya)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updatePesananKeFirebase(
        pesanan: pesanananmasuk,
        status: String,
        berat: String,
        biaya: String
    ) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("Orders")
            .child(pesanan.tanggal)
            .child(pesanan.jam)
            .orderByChild("orderId")
            .equalTo(pesanan.orderId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    data.ref.child("status").setValue(status)
                    data.ref.child("berat").setValue(berat)
                    data.ref.child("biaya").setValue(biaya)

                    val uid = data.child("uid").value.toString()

                    // Kirim Notifikasi ke Firebase dan Push Notification
                    kirimNotifikasiDanSimpan(
                        requireContext(),
                        uid, // Ganti dari userId ke uid
                        pesanan.orderId ?: "",
                        status,
                        berat,
                        biaya
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun kirimNotifikasiDanSimpan(
        context: Context,
        uid: String,
        orderId: String,
        status: String,
        berat: String,
        biaya: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val notifikasiRef = database.getReference("Notifikasi").child(uid).push()

        val pesan =
            "Pesanan Anda ($orderId) telah diperbarui.\nStatus: $status\nBerat: $berat kg\nBiaya: Rp $biaya"
        val waktu = System.currentTimeMillis()

        val dataNotifikasi = mapOf(
            "pesan" to pesan,
            "orderId" to orderId,
            "timestamp" to waktu
        )

        // Jalankan di background coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simpan notifikasi ke database
                notifikasiRef.setValue(dataNotifikasi).await()

                // Ambil token user berdasarkan UID
                val tokenSnapshot = database.getReference("Tokens").child(uid).get().await()
                val token = tokenSnapshot.getValue(String::class.java)

                if (!token.isNullOrEmpty()) {
                    // Kirim notifikasi via FCM v1
                    withContext(Dispatchers.IO) {
                        FcmV1Sender.sendNotificationToToken(
                            context = context,
                            targetToken = token,
                            title = "Cek Pesanan Anda",
                            body = pesan,
                            orderId = orderId
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("NotifikasiError", "Gagal kirim notifikasi: ${e.message}", e)
            }
        }
    }
}