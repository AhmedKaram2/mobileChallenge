package com.karam.mobilechallenge.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    @SerializedName("id")
    val id:Int ,
    @SerializedName("title")
    val title:String,
    @SerializedName("image")
    val image:String,
): Parcelable
