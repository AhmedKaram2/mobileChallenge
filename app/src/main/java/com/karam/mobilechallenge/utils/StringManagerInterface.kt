package com.karam.mobilechallenge.utils

// StringManagerInterface.kt
interface StringManager {
    fun getString(resourceId: Int): String
    fun getString(resourceId: Int, vararg formatArgs: Any): String
}

