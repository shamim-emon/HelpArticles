package com.shamim.helparticles

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails
import com.shamim.helparticles.data.network.ApiResult
import com.shamim.helparticles.data.network.ArticleRepository
import com.shamim.helparticles.presentation.navigation.AppNavHost
import com.shamim.helparticles.presentation.ui.TopBar
import com.shamim.helparticles.presentation.ui.theme.HelpArticlesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: ArticleRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelpArticlesTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = { TopBar(navController = navController) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        lifecycleScope.launch {
            repository.getArticles().collect { articleResponse->
                when(articleResponse) {
                    is ApiResult.Success<List<Article>> -> {
                        //articleResponse.data.isEmpty() retry
                        articleResponse.data.forEach { article ->
                            repository.getArticleDetails(article.id.toInt()).collect { detailsResponse->
                               when(detailsResponse) {
                                   is ApiResult.Success<ArticleDetails> -> {
                                       Log.i("EMON1234","${detailsResponse.data}")
                                   }
                                   is ApiResult.Error-> {
                                       //retry
                                   }

                                   else -> {}
                               }
                            }
                        }
                    }

                    is ApiResult.Error-> {
                        //retry
                    }

                    else -> {

                    }
                }
            }
        }
    }
}