package com.crome.forecastpoint.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                WeatherUpdateScheduler.applyFromPrefs(context)
                WorkManager.getInstance(context)
                    .enqueue(OneTimeWorkRequestBuilder<WeatherUpdateWorker>().build())
            } finally {
                pending.finish()
            }
        }
    }
}
