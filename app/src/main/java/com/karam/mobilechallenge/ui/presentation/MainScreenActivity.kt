
package com.karam.mobilechallenge.ui.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karam.mobilechallenge.Const
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.CategoryItems
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

        // Hoist the state of selectedItems and totalPrice
        var selectedItems by remember { mutableStateOf(listOf<CategoryItems>()) }

        // Calculate total price based on selected items
        val totalPrice by remember {
            derivedStateOf { selectedItems.sumOf { it.avgBudget.toDouble() } }
        }

        NavHost(navController, startDestination = Screens.CategoriesListScreen.route) {
            composable(Screens.CategoriesListScreen.route) {
                CategoriesListScreen(
                    viewModel = categoriesViewModel,
                    innerPadding = PaddingValues(AppSpacing.medium),
                    totalPrice = totalPrice,  // Pass down the total price
                    onCategorySelected = { category ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(Const.CATEGORY, category)
                        navController.navigate(Screens.CategoryItemsListScreen.route)
                    },
                    onSavedEventsClick = {
                        navController.navigate(Screens.EventSavedScreen.route)
                    }
                )
            }

            composable(Screens.CategoryItemsListScreen.route) {
                val category = navController.previousBackStackEntry?.savedStateHandle?.get<Category>(Const.CATEGORY)
                category?.let {
                    CategoryItemsListScreen(
                        viewModel = categoryItemsViewModel,
                        category = it,
                        totalPrice = totalPrice,
                        selectedItems = selectedItems,
                        onSelectedItemsChange = { newSelectedItems ->
                            selectedItems = newSelectedItems // Update selected items
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable(Screens.EventSavedScreen.route) {
                EventSavedScreen(totalPrice)
            }
        }
    }
}