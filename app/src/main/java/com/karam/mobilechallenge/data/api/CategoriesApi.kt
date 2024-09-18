package com.karam.mobilechallenge.data.api

import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.CategoryItems
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoriesApi {

    @GET("/categories.json")
    suspend fun getCategories(
    ):Response<List<Category>>

    @GET("/categories")
    suspend fun getCategoriesItems(
        @Query("categoryId") categoryId:Int
    ):Response<CategoryItems>
}