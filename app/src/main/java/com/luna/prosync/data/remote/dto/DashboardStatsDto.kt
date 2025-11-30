package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardStatsDto(
    val proyectosActivos: Int,
    val cambioProyectos: Int,
    val tareasCompletadas: Int,
    val cambioTareas: Int,
    val miembrosEquipo: Int,
    val cambioMiembros: Int,
    val tiempoPromedio: Double,
    val cambioTiempo: Double
)
