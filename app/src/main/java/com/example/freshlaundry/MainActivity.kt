package com.example.freshlaundry

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.freshlaundry.menu.NotifikasiFragment
import com.example.freshlaundry.menu.OrderLayananFragment
import com.example.freshlaundry.menu.PesananSayaFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatDelegate
import com.example.freshlaundry.menu.ChatingFragment

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set default fragment saat aplikasi dibuka
        if (savedInstanceState == null) {
            loadFragment(OrderLayananFragment())
        }
        // Listener untuk menu navigasi bawah
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_order -> loadFragment(OrderLayananFragment())
                R.id.nav_pesanan -> loadFragment(PesananSayaFragment())
                R.id.nav_notifikasi -> loadFragment(NotifikasiFragment())
                R.id.nav_chating -> loadFragment(ChatingFragment())
            }
            true
        }

        // Minta izin lokasi dulu
        requestLocationPermission()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun requestLocationPermission() {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Sudah diberi, langsung lanjut minta notifikasi
            requestNotificationPermission()
        }
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin lokasi diberikan", Toast.LENGTH_SHORT).show()
                    // Setelah izin lokasi diberikan, lanjut minta izin notifikasi
                    requestNotificationPermission()
                } else {
                    Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
                    // Bisa tetap lanjut minta notifikasi jika kamu mau walau lokasi ditolak
                    requestNotificationPermission()
                }
            }

            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}