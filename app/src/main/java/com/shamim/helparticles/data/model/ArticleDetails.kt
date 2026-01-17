package com.shamim.helparticles.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetails(
    val content: String,
    val id: String,
    val summary: String,
    val title: String,
    val updatedAt: Long
) {
    companion object {
        val EMPTY : ArticleDetails = ArticleDetails(
            content = "",
            id = "",
            summary = "",
            updatedAt = 0L,
            title = "",
        )
    }
}