package com.karam.mobilechallenge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karam.mobilechallenge.contract.intent.CategoryItemsIntent
import com.karam.mobilechallenge.contract.sideEffects.CategoryItemsSideEffects
import com.karam.mobilechallenge.contract.state.CategoryItemsState
import com.karam.mobilechallenge.data.useCases.CategoriesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CategoryItemsViewModel(
    private val categoryUseCase: CategoriesUseCase
) : ViewModel() {

    private val intent = Channel<CategoryItemsIntent>(Channel.UNLIMITED)
    private val _sideEffects = Channel<CategoryItemsSideEffects>(Channel.UNLIMITED)
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _state = MutableStateFlow<CategoryItemsState>(CategoryItemsState.Idle)
    val state: StateFlow<CategoryItemsState> get() = _state

    init {
        handleIntent()
    }

    fun setIntent(itemsIntent: CategoryItemsIntent) {
        viewModelScope.launch {
            intent.send(itemsIntent)
        }
    }

    fun runSideEffect(sideEffect: CategoryItemsSideEffects) {
        viewModelScope.launch {
            _sideEffects.send(sideEffect)
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is CategoryItemsIntent.FetchCategoryItemsFromAPI ->
                        handleFetchCategoriesItemsFromAPI(intent.categoryId)
                }
            }
        }
    }


    private fun handleFetchCategoriesItemsFromAPI(categoryID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // show loading
            _state.value = CategoryItemsState.Loading
            // fetch data from api
            fetchCategoriesItemsFromAPI(categoryID)
        }
    }

    private fun fetchCategoriesItemsFromAPI(categoryID: Int) {
        viewModelScope.launch {
            _state.value = try {
                val response = categoryUseCase.fetchCategoriesItems(categoryID)
                CategoryItemsState.CategoryItemsLoaded(response)

            } catch (e: Exception) {
                CategoryItemsState.Error(
                    message = (e as? HttpException)?.response()?.errorBody()?.string()
                        ?: e.message!!
                )
            }
        }
    }

}