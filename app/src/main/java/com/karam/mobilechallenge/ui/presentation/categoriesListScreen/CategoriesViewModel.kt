package com.karam.mobilechallenge.ui.presentation.categoriesListScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.contract.intent.CategoriesIntent
import com.karam.mobilechallenge.contract.sideEffects.CategorySideEffects
import com.karam.mobilechallenge.contract.state.CategoriesState
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.repository.CategoriesRepository
import com.karam.mobilechallenge.utils.StringManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the categories list screen.
 * Manages the business logic and state for displaying categories and handling user interactions.
 *
 * @property categoriesRepository Repository for fetching and managing category data.
 * @property stringManager Utility for managing string resources.
 */
class CategoriesViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val stringManager: StringManager
) : ViewModel() {

    // Flow for emitting side effects (like navigation events)
    private val _categorySideEffects = MutableSharedFlow<CategorySideEffects>()
    val categorySideEffects = _categorySideEffects.asSharedFlow()

    // State flows for managing loading and error states
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    /**
     * Combined state flow representing the current state of the categories screen.
     * Combines categories, total price, loading state, and error state.
     */
    val viewState: StateFlow<CategoriesState> = combine(
        categoriesRepository.categoriesFlow,
        categoriesRepository.totalPriceFlow,
        _isLoading,
        _error
    ) { categories, totalPrice, isLoading, error ->
        CategoriesState(
            isLoading = isLoading,
            categories = categories,
            totalPrice = totalPrice,
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

    /**
     * Handles intents (user actions) from the UI.
     *
     * @param intent The intent representing the user action.
     */
    fun handleIntent(intent: CategoriesIntent) {
        when (intent) {
            is CategoriesIntent.FetchCategoriesFromAPI -> fetchCategories()
            is CategoriesIntent.OpenEventsItemsScreen -> openEventsItemsScreen(intent.category)
            is CategoriesIntent.OpenEventsSavedScreen -> openEventsSavedScreen()
        }
    }

    /**
     * Fetches categories from the repository and updates the state.
     * Handles loading state and errors.
     */
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

    /**
     * Get The count of selected items for a specific category.
     */
    fun getCategoriesSelectedItemsCount(categoryId: Int): Int {
        return categoriesRepository.getCategoriesSelectedItemsCount(categoryId)
    }

    /**
     * Emits a side effect to open the events items screen for a specific category.
     *
     * @param category The category to open the events items screen for.
     */
    private fun openEventsItemsScreen(category: Category) {
        viewModelScope.launch {
            _categorySideEffects.emit(CategorySideEffects.OpenCategoriesItemsScreen(category))
        }
    }

    /**
     * Emits a side effect to open the saved events screen.
     */
    private fun openEventsSavedScreen() {
        viewModelScope.launch {
            _categorySideEffects.emit(CategorySideEffects.OpenSavedEventsScreen())
        }
    }
}