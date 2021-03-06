package com.inc.silence.weather.data.func

import android.content.Context
import com.inc.silence.weather.extension.networkInfo

class NetworkHandler(private val context: Context) {
    val isConnected get() = context.networkInfo?.isConnected
}