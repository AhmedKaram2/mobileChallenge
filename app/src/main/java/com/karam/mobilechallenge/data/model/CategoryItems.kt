package com.karam.mobilechallenge.data.model

import android.media.Image
import com.google.gson.annotations.SerializedName

data class CategoryItems(

    @SerializedName("id")
    val id:Int ,
    @SerializedName("title")
    val title:String,
    @SerializedName("image")
    val image: String,
    @SerializedName("minBudget")
    val minBudget: Int,
    @SerializedName("maxBudget")
    val maxBudget: Int,
    @SerializedName("avgBudget")
    val avgBudget: Int
)
