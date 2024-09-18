package com.karam.mobilechallenge.contract.intent

sealed class CategoriesIntent {

    class FetchCategoriesFromAPI() : CategoriesIntent()
    class FetchCategoriesItems(val categoryId:Int) : CategoriesIntent()
    class OpenEventsItemsScreen(val categoryId:Int) : CategoriesIntent()
}