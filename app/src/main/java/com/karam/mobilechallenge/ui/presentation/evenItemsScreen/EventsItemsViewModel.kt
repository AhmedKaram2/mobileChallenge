package com.karam.mobilechallenge.ui.presentation.evenItemsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.ui.contract.intent.EventsItemsIntent
import com.karam.mobilechallenge.ui.contract.state.EventsItemsState
import com.karam.mobilechallenge.data.repository.CategoriesRepository
import com.karam.mobilechallenge.utils.StringManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the event items screen.
 * Manages the business logic and state for displaying event items within a category and handling user interactions.
 *
 * @property categoriesRepository Repository for fetching and managing category and item data.
 * @property stringManager Utility for managing string resources.
 */
class EventsItemsViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val stringManager: StringManager
) : ViewModel() {

    private val _categoryId = MutableStateFlow<Int?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    /**
     * Combined state flow representing the current state of the event items screen.
     * Combines category items, total price, loading state, and error state.
     */
    val viewState: StateFlow<EventsItemsState> = combine(
        _categoryId,
        categoriesRepository.itemsFlow,
        categoriesRepository.totalPriceFlow,
        _isLoading,
        _error
    ) { categoryId, allItems, totalPrice, isLoading, error ->
        val items = categoryId?.let { allItems[it] } ?: emptyList()
        EventsItemsState(
            isLoading = isLoading,
            items = items,
            totalPrice = totalPrice,
            error = error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EventsItemsState(isLoading = true)
    )

    /**
     * Handles intents (user actions) from the UI.
     *
     * @param intent The intent representing the user action.
     */
    fun handleIntent(intent: EventsItemsIntent) {
        when (intent) {
            is EventsItemsIntent.FetchCategoryItemsFromAPI -> fetchCategoryItems(intent.categoryId)
            is EventsItemsIntent.ItemCheckedClick -> toggleItemSelection(intent.index)
        }
    }

    /**
     * Fetches items for a specific category from the repository and updates the state.
     * Handles loading state and errors.
     *
     * @param categoryId The ID of the category to fetch items for.
     */
    private fun fetchCategoryItems(categoryId: Int) {
        viewModelScope.launch {
            _categoryId.value = categoryId
            _isLoading.value = true
            _error.value = null
            try {
                categoriesRepository.getCategoriesItems(categoryId)
            } catch (e: Exception) {
                _error.value = e.message ?: stringManager.getString(R.string.error_fetching_items)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Toggles the selection state of an item at the given index.
     * Updates the repository and handles potential errors.
     *
     * @param index The index of the item to toggle in the current list of items.
     */
    private fun toggleItemSelection(index: Int) {
        val categoryId = _categoryId.value ?: return
        val items = viewState.value.items ?: return
        val item = items.getOrNull(index) ?: return
        viewModelScope.launch {
            try {
                categoriesRepository.toggleItemSelection(categoryId, item.id)
            } catch (e: Exception) {
                _error.value = e.message ?: stringManager.getString(R.string.error_toggling_item)
            }
        }
    }
}