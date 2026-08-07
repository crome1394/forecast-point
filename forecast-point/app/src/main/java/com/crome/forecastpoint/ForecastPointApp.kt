package com.crome.forecastpoint

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ForecastPointApp : Application() {
    /** App-wide IO scope for widget / boot work (survives Activity recreation). */
    val applicationScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @Volatile
        private var instance: ForecastPointApp? = null

        fun get(): ForecastPointApp =
            instance ?: error("ForecastPointApp not initialized")
    }
}
