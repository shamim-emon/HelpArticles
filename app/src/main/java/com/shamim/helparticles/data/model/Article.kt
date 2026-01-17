package com.shamim.helparticles.data.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class Article(
    val id: String,
    val summary: String,
    val title: String,
    val updatedAt: Long,
    val formattedDate:String = ""
)