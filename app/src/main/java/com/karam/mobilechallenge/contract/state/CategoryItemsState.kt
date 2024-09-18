package com.karam.mobilechallenge.contract.state

import com.karam.mobilechallenge.data.model.CategoryItems

sealed class CategoryItemsState {

    object Idle : CategoryItemsState()
    object Loading : CategoryItemsState()
    data class CategoryItemsLoaded(val items: List<CategoryItems>?) : CategoryItemsState()
    data class Error(val message: String) : CategoryItemsState()

}