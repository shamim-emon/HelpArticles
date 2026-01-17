package com.shamim.helparticles.presentation.ui

import android.content.res.Configuration
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.shamim.helparticles.data.model.ArticleDetails
import com.shamim.helparticles.data.network.ErrorResponse
import com.shamim.helparticles.data.network.FAKE_ARTICLE_DETAILS
import com.shamim.helparticles.presentation.DetailsUIState
import com.shamim.helparticles.presentation.ui.theme.HelpArticlesTheme
import io.noties.markwon.Markwon


@Composable
fun ArticleDetailsScreen(
    state: DetailsUIState,
    onLoadArticleDetails: ()->Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Column(
                modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {

                Text(
                    text = state.error.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onLoadArticleDetails) {
                    Text(text = "Retry")
                }
            }
        }

        state.articleDetails != ArticleDetails.EMPTY -> {
            val scrollState = rememberScrollState()

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarkdownText(state.articleDetails.content)
            }
        }
    }
}


@Composable
fun MarkdownText(markdown: String) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onBackground
    val markwon = remember { Markwon.create(context) }

    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                setTextIsSelectable(true)
                setTextColor(textColor.toArgb())
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = { textView ->
            textView.setTextColor(textColor.toArgb())
            markwon.setMarkdown(textView, markdown)
        }
    )
}

@Preview(
    name = "Details - Content (Light)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Details - Content (Dark)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DetailsScreenSuccessPreview() {
    HelpArticlesTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ArticleDetailsScreen(
                state = DetailsUIState(
                    articleDetails = FAKE_ARTICLE_DETAILS[0],
                    isLoading = false,
                    error = null
                ),
                onLoadArticleDetails = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(
    name = "Details -  Error (Light)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Details -  Error (Dark)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DetailsScreenClientErrorPreview() {
    HelpArticlesTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ArticleDetailsScreen(
                state = DetailsUIState(
                    articleDetails = ArticleDetails.EMPTY,
                    isLoading = false,
                    error = ErrorResponse(
                        errorTitle = "",
                        errorCode = 0,
                        errorMessage = "No internet connection",
                        isServerError = false
                    )
                ),
                onLoadArticleDetails = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

