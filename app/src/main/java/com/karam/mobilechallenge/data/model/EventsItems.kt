package com.karam.mobilechallenge.data.model

import com.google.gson.annotations.SerializedName

data class EventsItems(

    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("minBudget")
    val minBudget: Double,
    @SerializedName("maxBudget")
    val maxBudget: Double,
    @SerializedName("avgBudget")
    val avgBudget: Double,
    val isSelected: Boolean = false
)
