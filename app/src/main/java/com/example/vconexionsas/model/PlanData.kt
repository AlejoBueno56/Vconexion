package com.example.vconexionsas.model

data class PlanData(
    val title: String, // nombre_plan
    val speed: String, // megas + " MB"
    val price: String, // "$" + valor
    val features: List<String> = listOf("Soporte técnico", "Sin cláusulas", "Conectividad estable")
)

