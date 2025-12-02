package com.tuempresa.driverapp.data.network

data class ORSResponse(
    val features: List<Feature>
)

data class Feature(
    val geometry: Geometry
)

data class Geometry(
    val coordinates: List<List<Double>>
)
