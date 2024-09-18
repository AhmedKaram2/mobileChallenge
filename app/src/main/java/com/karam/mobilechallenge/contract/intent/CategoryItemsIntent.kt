package com.karam.mobilechallenge.contract.intent

sealed class CategoryItemsIntent {

    class FetchCategoryItemsFromAPI(val categoryId: Int) : CategoryItemsIntent()
}