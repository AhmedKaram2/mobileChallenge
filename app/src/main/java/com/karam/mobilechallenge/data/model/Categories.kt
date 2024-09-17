package com.karam.mobilechallenge.data.model

import com.google.gson.annotations.SerializedName

data class Categories(
    @SerializedName("id")
    val id:Int ,
    @SerializedName("title")
    val title:String,
    @SerializedName("image")
    val image:String,
)
