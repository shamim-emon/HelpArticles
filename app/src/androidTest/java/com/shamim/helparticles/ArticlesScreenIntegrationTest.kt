package com.shamim.helparticles

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.network.ErrorResponse
import com.shamim.helparticles.data.network.FAKE_ARTICLES
import com.shamim.helparticles.data.network.HTTP_200
import com.shamim.helparticles.data.network.HTTP_500
import com.shamim.helparticles.data.network.TestStubInterceptor
import com.shamim.helparticles.presentation.navigation.AppNavHost
import com.shamim.helparticles.presentation.ui.theme.HelpArticlesTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.serialization.builtins.ListSerializer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ArticlesScreenIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var interceptor: TestStubInterceptor



    @Before
    fun setup() {
        hiltRule.inject()
        interceptor.setResponse(
            path = "/articles",
            body = ErrorResponse(
                errorCode = HTTP_500,
                isServerError = true,
                errorTitle = "Internal Server error",
                errorMessage = "Something went wrong. Please try again later."
            ),
            serializer = ErrorResponse.serializer(),
            code = HTTP_500,
        )

    }


    @Test
    fun displaysErrorAndSuccessArticlesAfterRetry() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            HelpArticlesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppNavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Something went wrong. Please try again later.")
            .assertExists()
        composeTestRule.onNodeWithText("Retry").assertExists()

        composeTestRule.onNodeWithText("Retry").performClick()

        interceptor.setResponse(
            path = "/articles",
            body = FAKE_ARTICLES,
            serializer = ListSerializer(Article.serializer()),
            code = HTTP_200
        )
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("How to create an account").assertExists()
        composeTestRule.onNodeWithText("Account Creation Guide").assertExists()

    }
}