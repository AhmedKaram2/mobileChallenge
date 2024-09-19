package com.karam.mobilechallenge.contract.state

import com.karam.mobilechallenge.data.model.CategoryItems

data class EventsItemsState(
    val isLoading: Boolean = false,
    val items: List<CategoryItems>? = null,
    val totalPrice: Double = 0.0,
    val error: String? = null
)