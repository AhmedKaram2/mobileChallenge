@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.karam.mobilechallenge.ui.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karam.mobilechallenge.ui.theme.MobileChallengeTheme
import com.karam.mobilechallenge.ui.viewmodel.CategoriesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainScreenActivity :ComponentActivity(){
    private val categoriesViewModel: CategoriesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileChallengeTheme {

                HandleCategoriesScreenNavigation(
                    modifier = Modifier.fillMaxSize()
                )
//                CategoriesListScreen(
//                    viewModel = categoriesViewModel,
//                    innerPadding = PaddingValues(16.dp)
//                )
            }
        }
    }

    @Composable
    fun HandleCategoriesScreenNavigation(modifier: Modifier){
        val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
        NavigableListDetailPaneScaffold(
            modifier = modifier,
            navigator = navigator,
            listPane = {
                CategoriesListScreen(
                    viewModel = categoriesViewModel,
                    innerPadding = PaddingValues(16.dp),
                    navigator = navigator
                )
            },
            detailPane = {
                val content = navigator.currentDestination?.content?.toString() ?: "Select an option"
                ItemsListScreen(
                    navigator,
                    content
                )
            }
        )
    }

}