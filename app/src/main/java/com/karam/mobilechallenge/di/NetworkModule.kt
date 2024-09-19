package com.karam.mobilechallenge.di

import com.karam.mobilechallenge.BuildConfig
import com.karam.mobilechallenge.Const
import com.karam.mobilechallenge.data.api.CategoriesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {


    single<CategoriesApi> {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        //Base URL  is "https://swensonhe-dev-challenge.s3.us-west-2.amazonaws.com"
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofitBuilder.create(CategoriesApi::class.java)
    }
}