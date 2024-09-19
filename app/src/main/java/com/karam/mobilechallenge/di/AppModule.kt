package com.karam.mobilechallenge.di

import com.karam.mobilechallenge.utils.StringManager
import com.karam.mobilechallenge.utils.StringManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single<StringManager> { StringManagerImpl(androidContext()) }
}