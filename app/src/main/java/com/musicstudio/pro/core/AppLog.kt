package com.musicstudio.pro.core

import android.util.Log

object AppLog {
    private const val TAG = "MusicStudioPro"

    fun d(message: String) = Log.d(TAG, message)
    fun e(message: String, throwable: Throwable? = null) = Log.e(TAG, message, throwable)
}
