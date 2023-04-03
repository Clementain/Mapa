@file:Suppress("DEPRECATION")

package com.example.mapa

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mapa.API.Direcciones
import com.example.mapa.API.DireccionesApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline


class MainActivity : AppCompatActivity() {
    private var firstMarker: Marker? = null
    private lateinit var marker: Marker
    var map: MapView? = null

    private val direccionesApi: DireccionesApi by lazy {
        Direcciones.retrofitService
    }

    //your items
    var items = ArrayList<OverlayItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_main)

        map = findViewById<View>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)

        val mapController = map!!.controller
        mapController.setZoom(19)
        val startPoint = GeoPoint(20.140153689100682, -101.15067778465794)
        mapController.setCenter(startPoint)

        CoroutineScope(Dispatchers.IO).launch {
            val coordenadas = direccionesApi.getDirections()
            val features = coordenadas.features


            for (feature in features) {
                val geometry = feature.geometry
                val coordinates = geometry.coordinates


                println("Coordenadas de este Feature: $coordinates")

            }
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

        val line = Polyline()

        line.setPoints(
            arrayListOf(
                startPoint,
                GeoPoint(20.140462055482093, -101.15053861935188),
                GeoPoint(20.14341707158446, -101.14984874847927),
                GeoPoint(20.14395683454409, -101.15131101775268)
            )
        )
        map?.overlays?.add(line)
    }

    override fun onResume() {
        super.onResume()

        map!!.onResume()
    }

    override fun onPause() {
        super.onPause()

        map!!.onPause()
    }

}