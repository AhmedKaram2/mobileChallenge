package com.karam.mobilechallenge.di

import com.karam.mobilechallenge.ui.presentation.categoriesListScreen.CategoriesViewModel
import com.karam.mobilechallenge.ui.presentation.evenItemsScreen.EventsItemsViewModel
import com.karam.mobilechallenge.ui.presentation.eventSavedScreen.EventSavedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { CategoriesViewModel(get() , get()) }
    viewModel { EventsItemsViewModel(get(),get()) }
    viewModel { EventSavedViewModel(get()) }

}