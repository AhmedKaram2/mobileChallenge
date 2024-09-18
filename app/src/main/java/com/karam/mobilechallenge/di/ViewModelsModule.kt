package com.karam.mobilechallenge.di

import com.karam.mobilechallenge.ui.viewmodel.CategoriesViewModel
import com.karam.mobilechallenge.ui.viewmodel.CategoryItemsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { CategoriesViewModel(get()) }
    viewModel { CategoryItemsViewModel(get()) }

}