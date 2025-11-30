package com.luna.prosync.ui.screens.notifications

fun getNotificationTitle(type: String?): String {
    return when (type) {
        "TASK_ASSIGNED" -> "Tarea Asignada"
        "TASK_UPDATED" -> "Tarea Actualizada"
        "TASK_COMMENT" -> "Nuevo Comentario"
        "PROJECT_ADDED" -> "Proyecto Nuevo"
        "PROJECT_REMOVED" -> "Proyecto Eliminado"
        "ROLE_CHANGED" -> "Rol Actualizado"
        "DEADLINE_NEAR" -> "Fecha Límite Cercana"
        else -> type ?: "Notificación"
    }
}
