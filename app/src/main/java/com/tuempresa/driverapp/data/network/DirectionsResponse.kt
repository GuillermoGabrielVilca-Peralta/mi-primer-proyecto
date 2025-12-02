package com.tuempresa.driverapp.data.network

// Es una buena práctica importar la librería para usar @SerializedName
import com.google.gson.annotations.SerializedName

// Representa la respuesta completa de la API
data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>,
    @SerializedName("status") val status: String
)

// Representa una única ruta con sus detalles
data class Route(
    @SerializedName("overview_polyline") val overview_polyline: OverviewPolyline,
    @SerializedName("legs") val legs: List<Leg>
)

// Contiene la polilínea codificada (el string mágico)
data class OverviewPolyline(
    @SerializedName("points") val points: String
)

// Un tramo de la ruta, contiene la distancia y duración
data class Leg(
    @SerializedName("distance") val distance: Distance,
    @SerializedName("duration") val duration: Duration
)

data class Distance(
    @SerializedName("text") val text: String,
    // La API también devuelve el valor en metros, puede ser útil en el futuro
    @SerializedName("value") val value: Int
)

data class Duration(
    @SerializedName("text") val text: String,
    // La API también devuelve el valor en segundos, puede ser útil en el futuro
    @SerializedName("value") val value: Int
)
