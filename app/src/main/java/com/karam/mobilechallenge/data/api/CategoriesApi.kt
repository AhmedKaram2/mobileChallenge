package com.karam.mobilechallenge.data.api

import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.EventsItems
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoriesApi {

    @GET("/categories.json")
    suspend fun getCategories(
    ):Response<List<Category>>

    @GET("categories/{id}.json")
    suspend fun getCategoriesItems(
        @Path("id") categoryId:Int
    ):Response<List<EventsItems>>
}