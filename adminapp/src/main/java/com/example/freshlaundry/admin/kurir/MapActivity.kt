package com.example.freshlaundry.admin.kurir

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.freshlaundry.admin.R
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.InputStream
import kotlin.math.*

class MapActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sensorManager: SensorManager
    private lateinit var btnSelesai: Button
    private lateinit var btnTutup: Button
    private lateinit var txtInfo: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnNavigasi: Button
    private var isNavigating = false
    private var currentLocation: GeoPoint? = null
    private var lastLocation: GeoPoint? = null
    private var destinationPoint: GeoPoint? = null
    private var courierMarker: Marker? = null
    private var job: Job? = null
    private var graph: Map<GeoPoint, List<GeoPoint>> = emptyMap()
    private val locationThreshold = 5.0

    private var azimuth = 0f
    private var accelerometerValues: FloatArray? = null
    private var magneticFieldValues: FloatArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_map)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        map = findViewById(R.id.mapView)
        txtInfo = findViewById(R.id.txtInfo)
        btnSelesai = findViewById(R.id.btnSelesai)
        btnTutup = findViewById(R.id.btnTutup)
        progressBar = findViewById(R.id.progressBar)
        btnNavigasi = findViewById(R.id.btnNavigasi)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.isTilesScaledToDpi = true
        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.isEnabled = true
        map.overlays.add(rotationGestureOverlay)

        val lokasiString = intent.getStringExtra("lokasi")
        val namaCustomer = intent.getStringExtra("nama") ?: "Customer"

        lokasiString?.split(",")?.let {
            if (it.size == 2) {
                destinationPoint = GeoPoint(it[0].toDouble(), it[1].toDouble())
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()

        btnSelesai.setOnClickListener {
            Toast.makeText(this, "Penjemputan selesai untuk $namaCustomer", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnTutup.setOnClickListener {
            finish()
        }

        btnNavigasi.setOnClickListener {
            isNavigating = !isNavigating
            btnNavigasi.text = if (isNavigating) "Stop Navigasi" else "Navigasi"
            Toast.makeText(this, if (isNavigating) "Mode Navigasi Aktif" else "Navigasi Dimatikan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI)

        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        job?.cancel()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateIntervalMillis(2000L)
            .setWaitForAccurateLocation(true)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val newLocation = GeoPoint(location.latitude, location.longitude)

                if (lastLocation == null || newLocation.distanceToAsDouble(lastLocation) > locationThreshold) {
                    currentLocation = newLocation
                    updateRoute()
                    lastLocation = newLocation
                }

                if (courierMarker == null) {
                    courierMarker = Marker(map).apply {
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = resources.getDrawable(R.drawable.kurir_icon, null)
                    }
                    map.overlays.add(courierMarker)
                }

                courierMarker?.position = newLocation

                if (isNavigating) {
                    map.controller.animateTo(newLocation)
                    map.mapOrientation = -azimuth // Rotasi agar map mengikuti arah utara
                }

                map.invalidate()
            }
        }

        fusedLocationClient.requestLocationUpdates(request, locationCallback, mainLooper)
    }

    private fun updateRoute() {
        val start = currentLocation
        val end = destinationPoint ?: return

        progressBar.visibility = ProgressBar.VISIBLE
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            if (graph.isEmpty()) graph = loadGraphDataFromJson()
            val astar = AStar(graph, start!!, end)
            val path = astar.findPath()

            withContext(Dispatchers.Main) {
                progressBar.visibility = ProgressBar.GONE
                if (path.isEmpty()) {
                    txtInfo.text = "Rute tidak ditemukan"
                } else {
                    val fullPath = mutableListOf<GeoPoint>().apply {
                        add(start)
                        addAll(path)
                        add(end)
                    }

                    val polyline = Polyline().apply {
                        setPoints(fullPath)
                        outlinePaint.color = android.graphics.Color.BLUE
                        outlinePaint.strokeWidth = 8f
                    }

                    map.overlays.removeAll { it is Polyline }
                    map.overlays.add(polyline)

                    val markerEnd = Marker(map).apply {
                        position = end
                        title = "Tujuan"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    map.overlays.add(markerEnd)

                    map.controller.setZoom(18.0)
                    if (isNavigating) {
                        map.mapOrientation = -azimuth
                        map.controller.animateTo(start)
                    }

                    txtInfo.text = """
                        Estimasi jarak: ${estimateDistance(fullPath)} m
                        Estimasi waktu: ${estimateTimeMinutes(fullPath)} menit
                    """.trimIndent()

                    map.invalidate()
                }
            }
        }
    }

    private fun estimateDistance(path: List<GeoPoint>): Int {
        var total = 0.0
        for (i in 1 until path.size) {
            total += path[i - 1].distanceToAsDouble(path[i])
        }
        return total.roundToInt()
    }

    private fun estimateTimeMinutes(path: List<GeoPoint>): Int {
        val distance = estimateDistance(path)
        val avgSpeed = 30.0 * 1000 / 3600
        return (distance / avgSpeed / 60).roundToInt()
    }

    private fun loadGraphDataFromJson(): Map<GeoPoint, List<GeoPoint>> {
        val inputStream: InputStream = assets.open("graph_fresh_laundry.json")
        val json = JSONObject(inputStream.bufferedReader().use { it.readText() })

        val nodesJson = json.getJSONObject("nodes")
        val edgesJson = json.getJSONArray("edges")
        val nodes = mutableMapOf<Long, GeoPoint>()
        nodesJson.keys().forEach { key ->
            val node = nodesJson.getJSONObject(key)
            val x = node.getDouble("x")
            val y = node.getDouble("y")
            nodes[key.toLong()] = GeoPoint(y, x)
        }

        val edges = mutableMapOf<GeoPoint, MutableList<GeoPoint>>()
        for (i in 0 until edgesJson.length()) {
            val edge = edgesJson.getJSONObject(i)
            val from = edge.getLong("from")
            val to = edge.getLong("to")
            val fromGeo = nodes[from]
            val toGeo = nodes[to]
            if (fromGeo != null && toGeo != null) {
                edges.computeIfAbsent(fromGeo) { mutableListOf() }.add(toGeo)
                edges.computeIfAbsent(toGeo) { mutableListOf() }.add(fromGeo)
            }
        }

        return edges
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues = event.values
            Sensor.TYPE_MAGNETIC_FIELD -> magneticFieldValues = event.values
        }

        if (accelerometerValues != null && magneticFieldValues != null) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticFieldValues)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                val azimuthRad = orientation[0]
                azimuth = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                if (isNavigating) {
                    map.mapOrientation = -azimuth
                    map.invalidate()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
