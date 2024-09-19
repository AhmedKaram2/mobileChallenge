package com.karam.mobilechallenge.ui.contract.state

import com.karam.mobilechallenge.data.model.EventsItems

data class EventsItemsState(
    val isLoading: Boolean = false,
    val items: List<EventsItems>? = null,
    val totalPrice: Double = 0.0,
    val error: String? = null
)