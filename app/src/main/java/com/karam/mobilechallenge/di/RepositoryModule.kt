package com.karam.mobilechallenge.di

import com.karam.mobilechallenge.data.useCases.CategoriesUseCase
import com.karam.mobilechallenge.repository.CategoriesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoriesModule = module {

    factory{CategoriesUseCase(get())}

    single{ CategoriesRepository(get() , androidContext()) }

}