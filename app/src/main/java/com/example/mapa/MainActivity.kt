@file:Suppress("DEPRECATION")

package com.example.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mapa.API.Direcciones
import com.example.mapa.API.DireccionesApi
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem


class MainActivity : AppCompatActivity() {
    private var firstMarker: Marker? = null
    private var startPoint: GeoPoint? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    //private lateinit var marker: Marker
    var map: MapView? = null

    private val direccionesApi: DireccionesApi by lazy {
        Direcciones.retrofitService
    }

    //your items
    var items = ArrayList<OverlayItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_main)



        map = findViewById<View>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)

        val mapController = map!!.controller
        mapController.setZoom(19)
        startPoint = GeoPoint(0, 0)
        mapController.setCenter(startPoint)

// Inicializa la variable de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

// Configura el callback de ubicación
        locationCallback = object : LocationCallback() {
            @Suppress("NAME_SHADOWING")
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation
                startPoint = GeoPoint(lastLocation!!.latitude, lastLocation.longitude)
                mapController.setCenter(startPoint)
                firstMarker?.position = startPoint
                coords()
                map?.invalidate()
            }
        }

// Configura la solicitud de ubicación
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        items.add(
            OverlayItem(
                "Title", "Description", GeoPoint(0.0, 0.0)
            )
        )

        firstMarker = Marker(map)
        firstMarker?.position = startPoint
        firstMarker?.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
        firstMarker?.title = "Bello ITSUR"
        map?.overlays?.add(firstMarker)

        map?.invalidate()

        /*    val line = Polyline()

            line.setPoints(
                arrayListOf(
                    startPoint,
                    GeoPoint(20.140462055482093, -101.15053861935188),
                    GeoPoint(20.14341707158446, -101.14984874847927),
                    GeoPoint(20.14395683454409, -101.15131101775268)
                )
            )
            map?.overlays?.add(line)*/
    }

    override fun onResume() {
        super.onResume()

        map!!.onResume()
        startLocationUpdates()

    }

    override fun onPause() {
        super.onPause()

        map!!.onPause()
        stopLocationUpdates()

    }

    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return
                } else {
                    checkPermissions()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    fun coords() {
        CoroutineScope(Dispatchers.IO).launch {
            val inicio = "${startPoint!!.longitude},${startPoint!!.latitude}"
            val final = "-101.19970679248917,20.129289871695637"
            val api = "5b3ce3597851110001cf6248195446ce6bac45e7851606b557eab502"
            val coordenadas = direccionesApi.getDirections(api, inicio, final)
            val features = coordenadas.features

            for (feature in features) {
                val geometry = feature.geometry
                val coordinates = geometry.coordinates

                println("Coordenadas de este Feature: $coordinates")
            }
        }
    }

}