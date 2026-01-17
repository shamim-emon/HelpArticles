package com.shamim.helparticles.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.network.ErrorResponse
import com.shamim.helparticles.data.network.FAKE_ARTICLES
import com.shamim.helparticles.presentation.HomeUIState
import com.shamim.helparticles.presentation.ui.theme.HelpArticlesTheme

@Composable
fun ArticleScreen(
    state: HomeUIState,
    onLoadArticles: () -> Unit,
    onSearchQuery: (String) -> Unit,
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        var searchQuery by remember { mutableStateOf("") }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                onSearchQuery(it)
                searchQuery = it
            },
            label = { Text("Search articles") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error.errorMessage,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onLoadArticles) {
                            Text(text = "Retry")
                        }
                    }

                }

                state.filteredArticles.isEmpty() -> {
                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "No Articles found",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onLoadArticles) {
                            Text(text = "Retry")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.filteredArticles,
                            key = { it.id }
                        ) { article ->
                            ArticleItem(
                                article = article,
                                onArticleClick = onArticleClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleItem(
    article: Article,
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onArticleClick(article.id.toInt()) }),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(article.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(article.summary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Updated: ${article.formattedDate}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(
    name = "Articles – Content (Light)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Articles – Content (Dark)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ArticleScreenContentPreview() {
    HelpArticlesTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ArticleScreen(
                state = HomeUIState(
                    allArticles = FAKE_ARTICLES,
                    filteredArticles = FAKE_ARTICLES,
                    isLoading = false,
                    error = null
                ),
                onLoadArticles = {},
                onSearchQuery = {},
                onArticleClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(
    name = "Articles – Empty (Light)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Articles – Empty (Dark)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ArticleScreenEmptyPreview() {
    HelpArticlesTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ArticleScreen(
                state = HomeUIState(
                    allArticles = FAKE_ARTICLES,
                    filteredArticles = emptyList(),
                    isLoading = false,
                    error = null
                ),
                onLoadArticles = {},
                onSearchQuery = {},
                onArticleClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}


@Preview(
    name = "Articles – Error (Light)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Articles – Error (Dark)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ArticleScreenErrorPreview() {
    HelpArticlesTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ArticleScreen(
                state = HomeUIState(
                    allArticles = emptyList(),
                    filteredArticles = emptyList(),
                    isLoading = false,
                    error = ErrorResponse(
                        isServerError = false,
                        errorTitle = "",
                        errorCode = 0,
                        errorMessage = "Something went wrong. Please try again."
                    )
                ),
                onLoadArticles = {},
                onSearchQuery = {},
                onArticleClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
