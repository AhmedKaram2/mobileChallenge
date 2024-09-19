package com.karam.mobilechallenge.repository

import com.karam.mobilechallenge.data.api.CategoriesApi
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.CategoryItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Repository class responsible for managing categories and their items.
 * It acts as a single source of truth for the application's data layer.
 *
 * @property categoriesApi The API service for fetching categories and their items.
 */
class CategoriesRepository(
    private val categoriesApi: CategoriesApi,
) {
    // StateFlow to hold and emit the list of categories
    private val _categoriesFlow = MutableStateFlow<List<Category>>(emptyList())
    val categoriesFlow: StateFlow<List<Category>> = _categoriesFlow

    // StateFlow to hold and emit a map of category IDs to their respective items
    private val _itemsFlow = MutableStateFlow<Map<Int, List<CategoryItems>>>(emptyMap())
    val itemsFlow: StateFlow<Map<Int, List<CategoryItems>>> = _itemsFlow

    // StateFlow to hold and emit the total price of selected items
    private val _totalPriceFlow = MutableStateFlow(0.0)
    val totalPriceFlow: StateFlow<Double> = _totalPriceFlow

    /**
     * Fetches categories from the API and updates the categoriesFlow.
     */
    suspend fun fetchCategories() {
        val categories = categoriesApi.getCategories().body() ?: emptyList()
        _categoriesFlow.value = categories
    }

    /**
     * Fetches items for a specific category and updates the itemsFlow.
     * If the items for the category are already fetched, it doesn't fetch again.
     *
     * @param categoryId The ID of the category to fetch items for.
     */
    suspend fun getCategoriesItems(categoryId: Int) {
        if (!_itemsFlow.value.containsKey(categoryId)) {
            val items = categoriesApi.getCategoriesItems(categoryId = categoryId).body().orEmpty()
            _itemsFlow.update { currentMap ->
                currentMap + (categoryId to items)
            }
            updateTotalPrice()
        }
    }

    /**
     * Toggles the selection state of a specific item in a category.
     * Updates the itemsFlow and recalculates the total price.
     *
     * @param categoryId The ID of the category containing the item.
     * @param itemId The ID of the item to toggle.
     */
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
    }

    /**
     * Recalculates and updates the total price based on selected items.
     */
    private fun updateTotalPrice() {
        val newTotalPrice = _itemsFlow.value.values.flatten()
            .filter { it.isSelected }
            .sumOf { it.avgBudget }
        _totalPriceFlow.value = newTotalPrice
    }
}