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
import com.karam.mobilechallenge.ui.presentation.categoriesListScreen.CategoriesListScreen
import com.karam.mobilechallenge.ui.presentation.evenItemsScreen.EventsItemsListScreen
import com.karam.mobilechallenge.ui.theme.AppSpacing
import com.karam.mobilechallenge.ui.theme.MobileChallengeTheme
import com.karam.mobilechallenge.ui.presentation.categoriesListScreen.CategoriesViewModel
import com.karam.mobilechallenge.ui.presentation.evenItemsScreen.EventsItemsViewModel
import com.karam.mobilechallenge.ui.presentation.eventSavedScreen.EventSavedScreen
import com.karam.mobilechallenge.ui.presentation.eventSavedScreen.EventSavedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Main activity for the Event Builder application.
 * Sets up the navigation and manages the main ViewModels.
 */
class MainScreenActivity : ComponentActivity() {
    // ViewModels injected using Koin
    private val categoriesViewModel: CategoriesViewModel by viewModel()
    private val categoryItemsViewModel: EventsItemsViewModel by viewModel()
    private val savedEventsViewModel: EventSavedViewModel by viewModel()

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

    /**
     * Composable function that sets up the navigation for the Event Builder app.
     *
     * @param categoriesViewModel ViewModel for managing categories.
     * @param categoryItemsViewModel ViewModel for managing category items.
     */
    @Composable
    fun EventBuilderNavigation(
        categoriesViewModel: CategoriesViewModel,
        categoryItemsViewModel: EventsItemsViewModel
    ) {
        val navController = rememberNavController()

        NavHost(navController, startDestination = Screens.CategoriesListScreen.route) {
            // Categories List Screen
            composable(Screens.CategoriesListScreen.route) {
                CategoriesListScreen(
                    viewModel = categoriesViewModel,
                    innerPadding = PaddingValues(AppSpacing.medium),
                    onCategorySelected = { category ->
                        // Pass the selected category to the next screen
                        navController.currentBackStackEntry?.savedStateHandle?.set(Const.CATEGORY, category)
                        navController.navigate(Screens.CategoryItemsListScreen.route)
                    },
                    onSavedEventsClick = {
                        navController.navigate(Screens.EventSavedScreen.route)
                    }
                )
            }

            // Category Items List Screen
            composable(Screens.CategoryItemsListScreen.route) {
                val category = navController.previousBackStackEntry?.savedStateHandle?.get<Category>(Const.CATEGORY)
                category?.let {
                    EventsItemsListScreen(
                        viewModel = categoryItemsViewModel,
                        category = it,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            // Event Saved Screen
            composable(Screens.EventSavedScreen.route) {
                EventSavedScreen(savedEventsViewModel)
            }
        }
    }
}