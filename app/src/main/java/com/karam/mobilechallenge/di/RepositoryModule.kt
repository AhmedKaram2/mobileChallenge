package com.karam.mobilechallenge.di

import com.karam.mobilechallenge.data.repository.CategoriesRepository
import org.koin.dsl.module

val repositoriesModule = module {
    single{ CategoriesRepository(get()) }
}