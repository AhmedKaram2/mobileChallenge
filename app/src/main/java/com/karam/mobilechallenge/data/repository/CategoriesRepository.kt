package com.karam.mobilechallenge.data.repository

import com.karam.mobilechallenge.data.api.CategoriesApi
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.EventsItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CategoriesRepository(
    private val categoriesApi: CategoriesApi,
) {
    private val _categoriesFlow = MutableStateFlow<List<Category>>(emptyList())
    val categoriesFlow: StateFlow<List<Category>> = _categoriesFlow

    private val _itemsFlow = MutableStateFlow<Map<Int, List<EventsItems>>>(emptyMap())
    val itemsFlow: StateFlow<Map<Int, List<EventsItems>>> = _itemsFlow

    private val _totalPriceFlow = MutableStateFlow(0.0)
    val totalPriceFlow: StateFlow<Double> = _totalPriceFlow

    private val _selectedItemsCountFlow = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedItemsCountFlow: StateFlow<Map<Int, Int>> = _selectedItemsCountFlow

    suspend fun fetchCategories() {
        val categories = categoriesApi.getCategories().body() ?: emptyList()
        _categoriesFlow.value = categories
    }

    suspend fun getCategoriesItems(categoryId: Int) {
        if (!_itemsFlow.value.containsKey(categoryId)) {
            val items = categoriesApi.getCategoriesItems(categoryId = categoryId).body().orEmpty()
            _itemsFlow.update { currentMap ->
                currentMap + (categoryId to items)
            }
            updateTotalPrice()
            updateSelectedItemsCount()
        }
    }

    fun toggleItemSelection(categoryId: Int, itemId: Int) {
        _itemsFlow.update { currentMap ->
            currentMap.mapValues { (key, items) ->
                if (key == categoryId) {
                    items.map { item ->
                        if (item.id == itemId) {
                            item.copy(isSelected = !item.isSelected)
                        } else {
                            item
                        }
                    }
                } else {
                    items
                }
            }
        }
        updateTotalPrice()
        updateSelectedItemsCount()
    }

    private fun updateTotalPrice() {
        val newTotalPrice = _itemsFlow.value.values.flatten()
            .filter { it.isSelected }
            .sumOf { it.avgBudget }
        _totalPriceFlow.value = newTotalPrice
    }

    private fun updateSelectedItemsCount() {
        _selectedItemsCountFlow.update {
            _itemsFlow.value.mapValues { (_, items) ->
                items.count { it.isSelected }
            }
        }
    }
}