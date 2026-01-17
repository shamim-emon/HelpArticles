package com.shamim.helparticles.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class ArticleDetailsRoute(val id: Int)