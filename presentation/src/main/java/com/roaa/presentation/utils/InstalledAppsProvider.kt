package com.roaa.presentation.utils

import android.content.*
import com.roaa.presentation.utils.models.InstalledApp
import kotlinx.coroutines.*

object InstalledAppsProvider {
    suspend fun getInstalledApps(context: Context): List<InstalledApp> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            pm.queryIntentActivities(intent, 0)
                .map { info ->
                    InstalledApp(
                        name = info.loadLabel(pm).toString(),
                        packageName = info.activityInfo.packageName,
                        icon = info.loadIcon(pm)
                    )
                }
                .sortedBy { it.name.lowercase() }
        }
}