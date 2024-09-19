package com.karam.mobilechallenge.contract.intent

sealed class EventsItemsIntent {
    data class FetchCategoryItemsFromAPI(val categoryId: Int) : EventsItemsIntent()
    data class ItemCheckedClick(val index: Int) : EventsItemsIntent()
}