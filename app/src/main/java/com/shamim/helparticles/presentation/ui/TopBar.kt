package com.shamim.helparticles.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shamim.helparticles.presentation.navigation.ArticleDetailsRoute
import com.shamim.helparticles.presentation.navigation.HomeRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route ?: ""

    when {
        currentDestination.contains(HomeRoute::class.simpleName.toString()) -> {
            CenterAlignedTopAppBar(
                title = { Text(text = "Help Articles") }
            )
        }

        currentDestination.contains(ArticleDetailsRoute::class.simpleName.toString()) -> {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Article Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    }
}
