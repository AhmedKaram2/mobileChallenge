package com.karam.mobilechallenge.contract.state

import com.karam.mobilechallenge.data.model.Category

data class CategoriesState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val totalPrice: Double = 0.0,
    val error: String? = null
)