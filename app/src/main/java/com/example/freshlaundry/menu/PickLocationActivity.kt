package com.example.freshlaundry.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freshlaundry.R
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class PickLocationActivity : AppCompatActivity() {

    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_pick_location)

        map = findViewById(R.id.mapView)
        map.setMultiTouchControls(true)
        map.controller.setZoom(16.0)
        val startPoint = GeoPoint(-6.200000, 106.816666) // Default: Jakarta
        map.controller.setCenter(startPoint)

        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Lokasi Kamu"
        map.overlays.add(marker)

        map.setOnTouchListener { _, _ ->
            map.overlays.remove(marker)
            false
        }

        map.setOnLongClickListener {
            val geo = map.mapCenter as GeoPoint
            marker.position = geo
            map.overlays.add(marker)

            val lat = geo.latitude
            val lon = geo.longitude

            val resultIntent = Intent()
            resultIntent.putExtra("latitude", lat)
            resultIntent.putExtra("longitude", lon)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()

            true
        }
    }
}
