package com.luna.prosync.ui.model

import android.net.Uri

data class Attachment(
    val uri: Uri,
    val name: String,
    val size: Long,
    val type: String
)
