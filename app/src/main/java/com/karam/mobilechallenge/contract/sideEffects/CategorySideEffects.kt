package com.karam.mobilechallenge.contract.sideEffects

sealed class CategorySideEffects {

    class OpenCategoriesItemsScreen(val categoryId:Int) : CategorySideEffects()
}