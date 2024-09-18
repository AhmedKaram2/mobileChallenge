
package com.karam.mobilechallenge.ui.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karam.mobilechallenge.Const
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.ui.navigation.Screens
import com.karam.mobilechallenge.ui.theme.AppSpacing
import com.karam.mobilechallenge.ui.theme.MobileChallengeTheme
import com.karam.mobilechallenge.ui.viewmodel.CategoriesViewModel
import com.karam.mobilechallenge.ui.viewmodel.CategoryItemsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainScreenActivity :ComponentActivity(){
    private val categoriesViewModel: CategoriesViewModel by viewModel()
    private val categoryItemsViewModel: CategoryItemsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileChallengeTheme {
                EventBuilderNavigation(
                    categoriesViewModel = categoriesViewModel,
                    categoryItemsViewModel = categoryItemsViewModel
                )
            }
        }
    }

    @Composable
    fun EventBuilderNavigation(
        categoriesViewModel: CategoriesViewModel,
        categoryItemsViewModel: CategoryItemsViewModel
    ) {
        val navController = rememberNavController()

        NavHost(navController, startDestination = Screens.CategoriesListScreen.route) {
            composable(Screens.CategoriesListScreen.route) {
                CategoriesListScreen(
                    viewModel = categoriesViewModel,
                    innerPadding = PaddingValues(AppSpacing.medium),
                    onCategorySelected = { category ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(Const.CATEGORY, category)
                        navController.navigate(Screens.CategoryItemsListScreen.route)
                    }
                )
            }

            composable(Screens.CategoryItemsListScreen.route) {
                val category = navController.previousBackStackEntry?.savedStateHandle?.get<Category>(Const.CATEGORY)
                category?.let {
                    CategoryItemsListScreen(
                        viewModel = categoryItemsViewModel,
                        category = it,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}