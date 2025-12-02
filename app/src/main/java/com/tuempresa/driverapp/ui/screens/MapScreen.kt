package com.tuempresa.driverapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.PolyUtil
import com.tuempresa.driverapp.data.network.DirectionsApiService
import com.tuempresa.driverapp.data.network.DirectionsResponse
import com.tuempresa.driverapp.data.network.OpenRouteServiceApi
import com.tuempresa.driverapp.data.network.ORSResponse
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import com.tuempresa.driverapp.R

@Composable
fun MapScreen() {
    val arequipaLocation = LatLng(-16.4040102, -71.559611)
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(arequipaLocation, 12f)
    }
    val context = LocalContext.current
    val apiKey = remember { context.getString(R.string.MAPS_API_KEY) }

    // ================================
    // BLOQUE 1: UBICACIÓN Y PERMISOS
    // ================================
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var lastKnownLocation by remember { mutableStateOf<LatLng?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isFineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val isCoarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (isFineLocationGranted || isCoarseLocationGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        lastKnownLocation = LatLng(it.latitude, it.longitude)
                        Toast.makeText(context, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) {}
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { lastKnownLocation = LatLng(it.latitude, it.longitude) }
                    ?: Toast.makeText(context, "Activa la localización.", Toast.LENGTH_LONG).show()
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    LaunchedEffect(lastKnownLocation) {
        lastKnownLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1500
            )
        }
    }

    // ================================
    // BLOQUE 2: DIRECTIONS API Y OPENROUTESERVICE
    // ================================
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // --- GOOGLE DIRECTIONS ---
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val directionsApiService = remember { retrofit.create(DirectionsApiService::class.java) }

    fun getAndDrawRoute(origin: LatLng, destination: LatLng) {
        scope.launch {
            try {
                val originStr = "${origin.latitude},${origin.longitude}"
                val destStr = "${destination.latitude},${destination.longitude}"
                val response: Response<DirectionsResponse> =
                    directionsApiService.getDirections(originStr, destStr, apiKey)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == "OK") {
                        val route = body.routes[0]
                        routePoints = PolyUtil.decode(route.overview_polyline.points)
                        Toast.makeText(context, "Ruta Google obtenida", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("DirectionsAPI", e.message ?: "Error")
            }
        }
    }

    // --- OPENROUTESERVICE ---
    val orsRetrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val orsService = remember { orsRetrofit.create(OpenRouteServiceApi::class.java) }
    val orsApiKey = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6IjllZDFjMTlkZDIxYjQ2ZDRiZWVjYmZiMzA4MzE2NDRmIiwiaCI6Im11cm11cjY0In0="

    fun getRouteFromORS(origin: LatLng, destination: LatLng) {
        orsService.getRoute(orsApiKey,
            start = "${origin.longitude},${origin.latitude}",
            end = "${destination.longitude},${destination.latitude}"
        ).enqueue(object : Callback<ORSResponse> {
            override fun onResponse(call: Call<ORSResponse>, response: Response<ORSResponse>) {
                val coords = response.body()?.features?.firstOrNull()?.geometry?.coordinates ?: return
                routePoints = coords.map { LatLng(it[1], it[0]) } // ORS devuelve [lon, lat]
                Toast.makeText(context, "Ruta ORS obtenida", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ORSResponse>, t: Throwable) {
                Log.e("ORS", "Error: ${t.message}")
            }
        })
    }

    // ================================
    // BLOQUE 3: UI
    // ================================
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val mapProperties = MapProperties(
        mapType = mapType,
        isMyLocationEnabled = lastKnownLocation != null
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties
        ) {
            lastKnownLocation?.let {
                Marker(
                    state = rememberMarkerState(position = it),
                    title = "Mi Ubicación",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
            if (routePoints.isNotEmpty()) {
                Polyline(points = routePoints, color = Color.Blue, width = 15f)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { mapType = MapType.NORMAL }) { Text("Normal") }
                Button(onClick = { mapType = MapType.HYBRID }) { Text("Híbrido") }
                Button(onClick = { mapType = MapType.TERRAIN }) { Text("Terreno") }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { getCurrentLocation() }) { Text("Mi Ubicación") }
                Button(onClick = {
                    lastKnownLocation?.let { origin ->
                        val destination = LatLng(-16.432292, -71.509145)
                        getRouteFromORS(origin, destination) // Usar ORS
                    } ?: Toast.makeText(context, "Primero obtén tu ubicación actual", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Ruta al Mall Aventura")
                }
            }
        }
    }
}
