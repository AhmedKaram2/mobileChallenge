package com.karam.mobilechallenge.ui.presentation.eventSavedScreen

import androidx.lifecycle.ViewModel
import com.karam.mobilechallenge.repository.CategoriesRepository

class EventSavedViewModel(
    categoriesRepository: CategoriesRepository
): ViewModel(){

    val viewState = categoriesRepository.totalPriceFlow
}