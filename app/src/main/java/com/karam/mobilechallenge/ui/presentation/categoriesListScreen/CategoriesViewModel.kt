package com.karam.mobilechallenge.ui.presentation.categoriesListScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.ui.contract.intent.CategoriesIntent
import com.karam.mobilechallenge.ui.contract.sideEffects.CategorySideEffects
import com.karam.mobilechallenge.ui.contract.state.CategoriesState
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.repository.CategoriesRepository
import com.karam.mobilechallenge.utils.StringManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val stringManager: StringManager
) : ViewModel() {

    private val _categorySideEffects = MutableSharedFlow<CategorySideEffects>()
    val categorySideEffects = _categorySideEffects.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val viewState: StateFlow<CategoriesState> = combine(
        categoriesRepository.categoriesFlow,
        categoriesRepository.totalPriceFlow,
        categoriesRepository.selectedItemsCountFlow,
        _isLoading,
        _error
    ) { categories, totalPrice, selectedItemsCounts, isLoading, error ->
        CategoriesState(
            isLoading = isLoading,
            categories = categories,
            totalPrice = totalPrice,
            selectedItemsCounts = selectedItemsCounts,
            error = error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CategoriesState(isLoading = true)
    )

    init {
        fetchCategories()
    }

    fun handleIntent(intent: CategoriesIntent) {
        when (intent) {
            is CategoriesIntent.FetchCategoriesFromAPI -> fetchCategories()
            is CategoriesIntent.OpenEventsItemsScreen -> openEventsItemsScreen(intent.category)
            is CategoriesIntent.OpenEventsSavedScreen -> openEventsSavedScreen()
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                categoriesRepository.fetchCategories()
            } catch (e: Exception) {
                _error.value = e.message ?: stringManager.getString(R.string.an_unknown_error_occurred)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun openEventsItemsScreen(category: Category) {
        viewModelScope.launch {
            _categorySideEffects.emit(CategorySideEffects.OpenCategoriesItemsScreen(category))
        }
    }

    private fun openEventsSavedScreen() {
        viewModelScope.launch {
            _categorySideEffects.emit(CategorySideEffects.OpenSavedEventsScreen())
        }
    }
}