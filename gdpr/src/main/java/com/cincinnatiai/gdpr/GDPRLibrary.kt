package com.cincinnatiai.gdpr

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicLightColorScheme

data class GDPRConfig(
    val appTitle: String,
    val colorScheme: ColorScheme? = null,
    val typography: Typography? = null,
    val apiKey: String
)

class GDPRLibrary private constructor(
    internal val appTitle: String,
    internal val colorScheme: ColorScheme,
    internal val typography: Typography,
    internal val apiKey: String
) {

    companion object {

        @Volatile
        private var instance: GDPRLibrary? = null

        @RequiresApi(Build.VERSION_CODES.S)
        fun initialize(
            context: Context,
            config: GDPRConfig
        ) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GDPRLibrary(
                            config.appTitle, config.colorScheme ?: dynamicLightColorScheme(context),
                            (config.typography ?: Typography) as Typography,
                            config.apiKey
                        )
                    }
                }
            }
        }

        fun instance() = instance ?: throw IllegalStateException("Call Initialize First on GDPR Library")
    }
}