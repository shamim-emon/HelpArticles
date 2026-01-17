package com.shamim.helparticles.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.shamim.helparticles.presentation.DetailsIntent
import com.shamim.helparticles.presentation.DetailsViewModel
import com.shamim.helparticles.presentation.HomeIntent
import com.shamim.helparticles.presentation.HomeSideEffect
import com.shamim.helparticles.presentation.HomeViewModel
import com.shamim.helparticles.presentation.ui.ArticleDetailsScreen
import com.shamim.helparticles.presentation.ui.ArticleScreen


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {

            val homeViewModel = hiltViewModel<HomeViewModel>()
            val homeUIState by homeViewModel.uiState.collectAsStateWithLifecycle()

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        homeViewModel.onIntent(HomeIntent.LoadArticles)
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }



            LaunchedEffect(Unit) {
                homeViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        is HomeSideEffect.NavigateToDetails -> {
                            navController.navigate(ArticleDetailsRoute(effect.articleId))
                        }
                    }
                }
            }

            ArticleScreen(
                state = homeUIState,
                onLoadArticles = {homeViewModel.onIntent(HomeIntent.LoadArticles)},
                onSearchQuery = { homeViewModel.onIntent(HomeIntent.FilterArticles(it)) },
                onArticleClick = { homeViewModel.onIntent(HomeIntent.LoadDetails(it)) },
                modifier = modifier
            )
        }
        composable<ArticleDetailsRoute> { backStackEntry ->
            val details: ArticleDetailsRoute = backStackEntry.toRoute()
            val detailsViewModel = hiltViewModel<DetailsViewModel>()
            val detailsUIState by detailsViewModel.uiState.collectAsStateWithLifecycle()

            DisposableEffect(lifecycleOwner,details.id) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        detailsViewModel.onIntent(DetailsIntent.LoadArticleDetails(details.id))
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }


            ArticleDetailsScreen(
                state = detailsUIState,
                onLoadArticleDetails = { detailsViewModel.onIntent(DetailsIntent.LoadArticleDetails(details.id))},
                modifier = modifier
            )
        }
    }
}