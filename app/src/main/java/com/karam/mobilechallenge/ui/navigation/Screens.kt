package com.karam.mobilechallenge.ui.navigation

sealed class Screens(val route: String) {

    object CategoriesListScreen : Screens("categories_list_screen")
    object CategoryItemsListScreen : Screens("category_items_list_screen")

}