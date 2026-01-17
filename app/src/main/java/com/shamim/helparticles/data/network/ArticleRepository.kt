package com.shamim.helparticles.data.network

import com.shamim.helparticles.data.network.ApiResult
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticles(): Flow<ApiResult<List<Article>>>
    fun getArticleDetails(id: Int): Flow<ApiResult<ArticleDetails>>
}