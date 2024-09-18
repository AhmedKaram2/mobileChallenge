package com.karam.mobilechallenge.repository

import android.content.Context
import com.karam.mobilechallenge.Const
import com.karam.mobilechallenge.data.api.CategoriesApi
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.CategoryItems

class CategoriesRepository (private val categoriesApi: CategoriesApi
,private val context : Context){

    suspend fun getCategories() : List<Category>?
    {
        return categoriesApi.getCategories().body()
    }

    suspend fun getCategoriesItems(categoryId:Int)  : CategoryItems?
    {
        return categoriesApi.getCategoriesItems(
           categoryId= categoryId
        ).body()
    }

    fun saveSelectedCategories(selectedCategories: List<Category>) {
        val sharedPreferences = context.getSharedPreferences(Const.APP_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Extract category IDs and convert to a Set<Int>
        val selectedCategoryIds = selectedCategories.map { it.id }.toSet()

        // Convert Set<Int> to Set<String>
        val selectedCategoryIdsAsString = selectedCategoryIds.map { it.toString() }.toSet()

        // Store the Set<String> in SharedPreferences
        editor.putStringSet(Const.CATEGORIES_SHARED_PREF, selectedCategoryIdsAsString)

        // Apply the changes (asynchronously)
        editor.apply()
    }

    fun saveSelectedCategory(category: Category) {
        val sharedPreferences = context.getSharedPreferences(Const.APP_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve existing saved category IDs
        val savedCategoryIds = sharedPreferences.getStringSet(Const.CATEGORIES_SHARED_PREF, emptySet()) ?: emptySet()

        // Add the new category ID to the set (if not already present)
        val updatedCategoryIds = savedCategoryIds.toMutableSet().apply {
            add(category.id.toString())
        }

        // Store the updated set back in SharedPreferences
        editor.putStringSet(Const.CATEGORIES_SHARED_PREF, updatedCategoryIds)

        // Apply the changes
        editor.apply()
    }


    fun loadSelectedCategories(allCategories: List<Category>): List<Category> {
        val sharedPreferences = context.getSharedPreferences(Const.APP_PREF, Context.MODE_PRIVATE)

        // Retrieve the Set<String> from SharedPreferences
        val savedCategoryIdsAsString = sharedPreferences.getStringSet(Const.CATEGORIES_SHARED_PREF, emptySet()) ?: emptySet()

        // Convert Set<String> to Set<Int>
        val savedCategoryIds = savedCategoryIdsAsString.map { it.toInt() }.toSet()

        // Filter allCategories based on saved IDs
        return allCategories.filter { it.id in savedCategoryIds }
    }
}