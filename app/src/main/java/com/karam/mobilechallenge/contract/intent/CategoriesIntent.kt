package com.karam.mobilechallenge.contract.intent

import com.karam.mobilechallenge.data.model.Category

sealed class CategoriesIntent {

    class FetchCategoriesFromAPI : CategoriesIntent()
    class OpenEventsItemsScreen(val category:Category) : CategoriesIntent()
    class OpenEventsSavedScreen : CategoriesIntent()
}