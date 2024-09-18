package com.karam.mobilechallenge.contract.sideEffects

import com.karam.mobilechallenge.data.model.Category

sealed class CategorySideEffects {

    class OpenCategoriesItemsScreen(val category:Category) : CategorySideEffects()
    class OpenSavedEventsScreen : CategorySideEffects()
}