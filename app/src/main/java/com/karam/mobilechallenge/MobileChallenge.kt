package com.karam.mobilechallenge

import android.app.Application
import com.karam.mobilechallenge.di.networkModule
import com.karam.mobilechallenge.di.repositoriesModule
import com.karam.mobilechallenge.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MobileChallenge : Application(){

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@MobileChallenge)
            modules(
                listOf(
                    networkModule,
                    repositoriesModule,
                    viewModelsModule

                )
            )
        }

    }
}