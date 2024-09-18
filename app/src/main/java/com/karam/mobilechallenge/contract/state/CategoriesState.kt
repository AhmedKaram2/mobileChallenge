package com.karam.mobilechallenge.contract.state

import com.karam.mobilechallenge.data.model.Category

sealed class CategoriesState {

    object Idle : CategoriesState()
    object Loading : CategoriesState()
    data class Error(val message: String) : CategoriesState()
    data class CategoriesLoaded(val categories: List<Category>?) : CategoriesState()

}