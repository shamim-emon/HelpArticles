package com.shamim.helparticles.background

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails
import com.shamim.helparticles.data.network.ApiResult
import com.shamim.helparticles.data.network.ArticleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

@HiltWorker
class FetchArticlesWorker @AssistedInject constructor(
    private val repository: ArticleRepository,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "FetchArticlesWorker"
        private const val MAX_CONCURRENCY = 5
    }

    override suspend fun doWork(): Result = try {
        val articlesResponse = repository.getArticles().first()
        when (articlesResponse) {
            is ApiResult.Success<List<Article>> -> {
                articlesResponse.data.chunked(MAX_CONCURRENCY).forEach { chunk ->
                    coroutineScope {
                        chunk.map { article ->
                            async { fetchArticleDetails(article) }
                        }.awaitAll()
                    }
                }
            }
            is ApiResult.Error -> throw Exception("Failed to fetch articles")
            else -> Unit
        }

        Result.success()
    } catch (e: Exception) {
        e.printStackTrace()
        Result.retry()
    }

    private suspend fun fetchArticleDetails(article: Article) {
        val detailsResponse = repository.getArticleDetails(article.id.toInt()).first()
        when (detailsResponse) {
            is ApiResult.Success<ArticleDetails> -> Log.i(TAG, "${detailsResponse.data}")
            is ApiResult.Error -> throw Exception("Failed to fetch details for article ${article.id}")
            else -> Unit
        }
    }
}


