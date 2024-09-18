package com.karam.mobilechallenge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karam.mobilechallenge.contract.intent.CategoriesIntent
import com.karam.mobilechallenge.contract.sideEffects.CategorySideEffects
import com.karam.mobilechallenge.contract.state.CategoriesState
import com.karam.mobilechallenge.data.useCases.CategoriesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CategoriesViewModel(
    private val categoryUseCase: CategoriesUseCase
    ) : ViewModel() {

    private val categoriesIntent = Channel<CategoriesIntent>(Channel.UNLIMITED)
    private val _categorySideEffects = Channel<CategorySideEffects>(Channel.UNLIMITED)
    val  categorySideEffects = _categorySideEffects.receiveAsFlow()

    private val _state = MutableStateFlow<CategoriesState>(CategoriesState.Idle)
    val state: StateFlow<CategoriesState> get() = _state

    init {
        handleIntent()
    }

    fun setIntent(intent: CategoriesIntent) {
        viewModelScope.launch {
            categoriesIntent.send(intent)
        }
    }

    fun runSideEffect(sideEffect: CategorySideEffects) {
        viewModelScope.launch {
            _categorySideEffects.send(sideEffect)
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            categoriesIntent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is CategoriesIntent.FetchCategoriesFromAPI -> handleFetchCategoriesFromAPI()
                    is CategoriesIntent.OpenEventsItemsScreen -> runSideEffect(CategorySideEffects
                        .OpenCategoriesItemsScreen(intent.category))
                    is CategoriesIntent.OpenEventsSavedScreen -> runSideEffect(CategorySideEffects.OpenSavedEventsScreen())
                }
            }
        }
    }
    private fun handleFetchCategoriesFromAPI() {
        viewModelScope.launch(Dispatchers.IO){
            // show loading
            _state.value = CategoriesState.Loading
            // fetch data from api
            fetchCategoriesFromAPI()
        }
    }

    private fun fetchCategoriesFromAPI() {
        viewModelScope.launch {
            _state.value = try {
                val  response = categoryUseCase.fetchCategoriesFromAPI()
                CategoriesState.CategoriesLoaded(response)

            }catch (e : Exception){
                CategoriesState.Error(
                    message = (e as? HttpException)?.response()?.errorBody()?.string() ?: e.message!!
                )
            }
        }
    }
}