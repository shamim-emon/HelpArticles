package com.shamim.helparticles.data.network

import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

interface ArticleApiService {

    @GET("articles")
    suspend fun getArticles(): Response<List<Article>>

    @GET("article/{id}")
    suspend fun getArticleDetails(@Path("id") id: Int): Response<ArticleDetails>
}