package com.karam.mobilechallenge.data.api

import com.karam.mobilechallenge.data.model.Categories
import com.karam.mobilechallenge.data.model.CategoryItems
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoriesApi {

    @GET("/categories")
    suspend fun getCategories():Response<Categories>

    @GET("/categories")
    suspend fun getCategoriesItems(
        @Query("categoryId") categoryId:Int
    ):Response<CategoryItems>
}