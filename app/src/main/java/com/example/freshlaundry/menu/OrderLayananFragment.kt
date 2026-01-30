package com.example.freshlaundry.menu

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.freshlaundry.R
import com.example.freshlaundry.model.Layanan
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
private const val REQUEST_LOCATION_PICKER = 1001
private var selectedEtLokasi: EditText? = null

class OrderLayananFragment : Fragment() {

    private val daftarLayanan = listOf(
        Layanan("Cuci Setrika", "Rp6.000/kg", "2-4 hari"),
        Layanan("Setrika", "Rp3.500/kg", "1-3 hari"),
        Layanan("Cuci Kering", "Rp4.000/kg", "3-4 hari"),
        Layanan("Cuci Basah", "Rp3.500/kg", "3-4 hari")
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val userEmail = auth.currentUser?.email ?: "user"
        val userName = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

        val greetingText = TextView(requireContext()).apply {
            text = "Halo, $userName 👋"
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.black))
            setPadding(0, 0, 0, 24)
        }

        mainLayout.addView(greetingText)

        val layoutLayanan = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }

        daftarLayanan.forEach { layanan ->
            val card = createLayananCard(layanan)
            layoutLayanan.addView(card)
        }

        mainLayout.addView(layoutLayanan)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return mainLayout
    }

    private fun createLayananCard(layanan: Layanan): CardView {
        val context = requireContext()

        val cardView = CardView(context).apply {
            radius = 16f
            cardElevation = 8f
            useCompatPadding = true
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params
        }

        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val tvNama = TextView(context).apply {
            text = layanan.nama
            textSize = 18f
            setTextColor(resources.getColor(android.R.color.black))
        }

        val tvHarga = TextView(context).apply {
            text = "Harga: ${layanan.hargaPerKg}"
            textSize = 14f
        }

        val tvEstimasi = TextView(context).apply {
            text = "Estimasi: ${layanan.estimasiHari}"
            textSize = 14f
        }

        val btnOrder = Button(context).apply {
            text = "Order"
            setOnClickListener {
                showOrderDialog(layanan.nama)
            }
        }

        contentLayout.apply {
            addView(tvNama)
            addView(tvHarga)
            addView(tvEstimasi)
            addView(btnOrder)
        }

        cardView.addView(contentLayout)
        return cardView
    }

    @SuppressLint("MissingPermission")
    private fun showOrderDialog(namaLayanan: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order, null)
        val etNama = dialogView.findViewById<EditText>(R.id.etNama)
        val etLokasi = dialogView.findViewById<EditText>(R.id.etLokasi)
        val spinnerJam = dialogView.findViewById<Spinner>(R.id.spinnerJam)
        val etCatatan = dialogView.findViewById<EditText>(R.id.etCatatan)
        val btnKonfirmasi = dialogView.findViewById<Button>(R.id.btnKonfirmasi)
        val btnPilihLokasi = dialogView.findViewById<Button>(R.id.btnPilihLokasi) // Tambahan
        selectedEtLokasi = etLokasi

        btnPilihLokasi.setOnClickListener {
            val intent = Intent(requireContext(), MapPickerActivity::class.java)
            startActivityForResult(intent, REQUEST_LOCATION_PICKER)
        }

        val semuaJam = generateJamList()
        val hariIni = getTodayString()

        database.child("Orders").child(hariIni).get().addOnSuccessListener { snapshot ->
            val jamTerpakai = snapshot.children.mapNotNull { it.key }
            val jamTersedia = semuaJam.filterNot { jamTerpakai.contains(it) }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, jamTersedia)
            spinnerJam.adapter = adapter
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Order $namaLayanan")
            .create()

        btnKonfirmasi.setOnClickListener {
            val nama = etNama.text.toString()
            val lokasi = etLokasi.text.toString()
            val catatan = etCatatan.text.toString()
            val jam = spinnerJam.selectedItem?.toString()

            if (nama.isEmpty() || lokasi.isEmpty() || jam == null) {
                Toast.makeText(requireContext(), "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val orderId = UUID.randomUUID().toString() // ✅ ID unik acak
            val orderData = mapOf(
                "orderId" to orderId,
                "uid" to uid,
                "nama" to nama,
                "layanan" to namaLayanan,
                "lokasi" to lokasi,
                "jam" to jam,
                "catatan" to catatan,
                "tanggal" to hariIni
            )


            database.child("Orders").child(hariIni).child(jam).push().setValue(orderData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Order berhasil!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal order", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION_PICKER && resultCode == AppCompatActivity.RESULT_OK) {
            val lat = data?.getDoubleExtra("latitude", 0.0)
            val lon = data?.getDoubleExtra("longitude", 0.0)
            selectedEtLokasi?.setText("$lat, $lon")
        }
    }

    private fun generateJamList(): List<String> {
        val jamList = mutableListOf<String>()
        var hour = 9
        var minute = 0
        while (hour < 16 || (hour == 16 && minute == 0)) {
            jamList.add(String.format("%02d:%02d", hour, minute))
            minute += 30
            if (minute >= 60) {
                minute = 0
                hour++
            }
        }
        return jamList
    }

    private fun getTodayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
