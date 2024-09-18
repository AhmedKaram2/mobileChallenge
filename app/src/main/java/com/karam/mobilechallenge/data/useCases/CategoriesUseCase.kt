package com.karam.mobilechallenge.data.useCases

import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.CategoryItems
import com.karam.mobilechallenge.repository.CategoriesRepository

class CategoriesUseCase(private val categoriesRepository: CategoriesRepository) {

    // Fetch categories from the repository that call the API
    suspend fun fetchCategoriesFromAPI() : List<Category>?
    {
        return categoriesRepository.getCategories()
    }

    // Fetch categories items from the repository that call the API
    suspend fun fetchCategoriesItems(categoryId:Int) : CategoryItems?
    {
        return categoriesRepository.getCategoriesItems(categoryId)
    }

    // Save selected category to shared preferences
    fun saveSelectedCategoryToList(categories: Category) {
        categoriesRepository.saveSelectedCategory(categories)
    }


}