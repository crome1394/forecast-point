package com.crome.forecastpoint.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.crome.forecastpoint.data.PreferencesRepository
import com.crome.forecastpoint.data.WeatherRepository
import java.util.concurrent.TimeUnit

class WeatherUpdateWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val prefs = PreferencesRepository(applicationContext)
        if (!prefs.getAutoUpdateOnce()) {
            return Result.success()
        }
        return try {
            val repo = WeatherRepository(applicationContext)
            val snap = repo.refreshActive(manual = false)
            if (snap != null) Result.success() else Result.retry()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val UNIQUE_NAME = "noaa_forecast_periodic_update"
    }
}

object WeatherUpdateScheduler {
    fun reconcile(context: Context) {
        // Fire-and-forget: read prefs via blocking isn't ideal; schedule default and let
        // settings screen re-schedule with exact interval.
        // Actual enable/interval applied from suspend functions below via WorkManager.
    }

    suspend fun applyFromPrefs(context: Context) {
        val prefs = PreferencesRepository(context)
        if (prefs.getAutoUpdateOnce()) {
            schedule(context, prefs.getIntervalOnce())
        } else {
            cancel(context)
        }
    }

    fun schedule(context: Context, intervalMinutes: Int) {
        val minutes = intervalMinutes.coerceIn(15, 24 * 60).toLong()
        val request = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(
            minutes,
            TimeUnit.MINUTES,
        ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WeatherUpdateWorker.UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WeatherUpdateWorker.UNIQUE_NAME)
    }
}
