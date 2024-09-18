package com.karam.mobilechallenge.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    val id:Int ,
    @SerializedName("title")
    val title:String,
    @SerializedName("image")
    val image:String,
    var isSelected: Boolean = false // Track selection status
)
