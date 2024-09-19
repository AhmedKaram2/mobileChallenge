package com.karam.mobilechallenge.ui.presentation.evenItemsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.contract.intent.EventsItemsIntent
import com.karam.mobilechallenge.contract.state.EventsItemsState
import com.karam.mobilechallenge.repository.CategoriesRepository
import com.karam.mobilechallenge.utils.StringManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EventsItemsViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val stringManager: StringManager

) : ViewModel() {

    private val _categoryId = MutableStateFlow<Int?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

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

    fun handleIntent(intent: EventsItemsIntent) {
        when (intent) {
            is EventsItemsIntent.FetchCategoryItemsFromAPI -> fetchCategoryItems(intent.categoryId)
            is EventsItemsIntent.ItemCheckedClick -> toggleItemSelection(intent.index)
        }
    }

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

    private fun toggleItemSelection(index: Int) {
        val categoryId = _categoryId.value ?: return
        val items = viewState.value.items ?: return
        val item = items.getOrNull(index) ?: return
        viewModelScope.launch {
            try {
                categoriesRepository.toggleItemSelection(categoryId, item.id)
            } catch (e: Exception) {
                _error.value = e.message ?:  stringManager.getString(R.string.error_toggling_item)
            }
        }
    }
}