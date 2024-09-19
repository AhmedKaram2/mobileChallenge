package com.karam.mobilechallenge.ui.contract.intent

import com.karam.mobilechallenge.data.model.Category

sealed class CategoriesIntent {
    object FetchCategoriesFromAPI : CategoriesIntent()
    data class OpenEventsItemsScreen(val category: Category) : CategoriesIntent()
    object OpenEventsSavedScreen : CategoriesIntent()
}