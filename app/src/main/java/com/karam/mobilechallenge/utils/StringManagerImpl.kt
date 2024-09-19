package com.karam.mobilechallenge.utils

// StringManagerImpl.kt
import android.content.Context
import androidx.annotation.StringRes

class StringManagerImpl(private val context: Context) : StringManager {
    override fun getString(@StringRes resourceId: Int): String {
        return context.getString(resourceId)
    }

    override fun getString(@StringRes resourceId: Int, vararg formatArgs: Any): String {
        return context.getString(resourceId, *formatArgs)
    }
}