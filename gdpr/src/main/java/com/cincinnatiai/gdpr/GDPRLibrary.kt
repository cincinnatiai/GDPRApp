package com.cincinnatiai.gdpr

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import com.cincinnatiai.gdpr.network.NetworkModule
import com.cincinnatiai.gdpr.usecase.AccountSelectedUseCase
import com.cincinnatiai.gdpr.usecase.AccountSelectedUseCaseImpl
import com.cincinnatiai.gdpr.usecase.CreateDeleteRequestUseCase
import com.cincinnatiai.gdpr.usecase.CreateDeleteRequestUseCaseImpl
import com.cincinnatiai.gdpr.usecase.CreateInformationRequestUseCase
import com.cincinnatiai.gdpr.usecase.CreateInformationRequestUseCaseImpl

data class GDPRConfig(
    val isDebug: Boolean,
    val appTitle: String,
    val colorScheme: ColorScheme? = null,
    val clientId: String,
    val apiKey: String,
)

interface GDPRLibraryListener {
    fun onUserDeleteRequested(email: String)
}

class GDPRLibrary private constructor(
    internal val isDebug: Boolean = false,
    internal val appTitle: String,
    internal val colorScheme: ColorScheme,
    internal val clientId: String,
    internal val apiKey: String,
    internal val gdprDataProvider: GDPRDataProvider,
    internal val gdprListener: GDPRLibraryListener
) {

    private val networkModule: NetworkModule by lazy {
        NetworkModule(isDebug, gdprDataProvider)
    }

    val accountSelectedUseCase: AccountSelectedUseCase by lazy {
        AccountSelectedUseCaseImpl()
    }

    val createInfoUseCase: CreateInformationRequestUseCase by lazy {
        CreateInformationRequestUseCaseImpl(networkModule.gdprApi, apiKey)
    }

    val createDeleteUseCase: CreateDeleteRequestUseCase by lazy {
        CreateDeleteRequestUseCaseImpl(networkModule.gdprApi, apiKey)
    }

    companion object {

        @Volatile
        private var instance: GDPRLibrary? = null

        @RequiresApi(Build.VERSION_CODES.S)
        fun initialize(
            context: Context,
            config: GDPRConfig,
            gdprDataProvider: GDPRDataProvider,
            gdprLibraryListener: GDPRLibraryListener
        ) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GDPRLibrary(
                            config.isDebug,
                            config.appTitle, config.colorScheme ?: dynamicLightColorScheme(context),
                            config.clientId,
                            config.apiKey,
                            gdprDataProvider,
                            gdprLibraryListener
                        )
                    }
                }
            }
        }

        fun instance() =
            instance ?: throw IllegalStateException("Call Initialize First on GDPR Library")
    }
}