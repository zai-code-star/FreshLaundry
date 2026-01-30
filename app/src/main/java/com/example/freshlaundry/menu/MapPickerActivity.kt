package com.example.freshlaundry.menu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.freshlaundry.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapPickerActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.mapView)
        val btnPilih = findViewById<Button>(R.id.btnPilihLokasi)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(17.0)

        // Setup marker default (akan diperbarui ke lokasi user nanti)
        marker = Marker(map).apply {
            position = GeoPoint(-6.200000, 106.816666) // Jakarta default
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        map.overlays.add(marker)

        // Geser map = pindah marker
        map.setOnTouchListener { _, _ ->
            marker.position = map.mapCenter as GeoPoint
            map.invalidate()
            false
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissionAndMoveMap()

        btnPilih.setOnClickListener {
            val center = map.mapCenter as GeoPoint

            // Lokasi pusat layanan (laundry)
            val pusat = GeoPoint(-7.7101574, 110.779557)

            // Hitung jarak dari lokasi user ke titik pusat
            val distance = center.distanceToAsDouble(pusat)

            if (distance > 10000) { // lebih dari 10KM
                Toast.makeText(this, "Mohon Maaf, area Anda di luar jangkauan kami", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Jika dalam radius, lanjutkan kirim hasil ke activity sebelumnya
            val resultIntent = Intent().apply {
                putExtra("latitude", center.latitude)
                putExtra("longitude", center.longitude)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun checkLocationPermissionAndMoveMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLocation = GeoPoint(location.latitude, location.longitude)
                map.controller.animateTo(userLocation)
                marker.position = userLocation
                map.invalidate()
            } else {
                Toast.makeText(this, "Gagal mendapatkan lokasi. Pastikan GPS aktif", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Jika user baru memberikan permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationPermissionAndMoveMap()
        } else {
            Toast.makeText(this, "Izin lokasi diperlukan untuk menampilkan lokasi Anda", Toast.LENGTH_SHORT).show()
        }
    }
}
