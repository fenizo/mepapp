package com.mepapp.mobile.worker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mepapp.mobile.data.AuthRepository
import com.mepapp.mobile.network.HeartbeatRequest
import com.mepapp.mobile.network.MepApiService
import com.mepapp.mobile.network.NetworkModule
import kotlinx.coroutines.flow.first

class HeartbeatWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "HeartbeatWorker"
        const val WORK_NAME = "heartbeat_work"
    }

    private val apiService = NetworkModule.createService<MepApiService>()

    override suspend fun doWork(): Result {
        return try {
            val authRepository = AuthRepository(applicationContext)
            val token = authRepository.authToken.first()

            if (token.isNullOrBlank()) {
                Log.d(TAG, "No auth token, skipping heartbeat")
                return Result.success() // Don't retry if not logged in
            }

            NetworkModule.setAuthToken(token)

            val userId = authRepository.userId.first() ?: return Result.success()

            // Get user info
            val userInfo = try {
                apiService.getMe()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get user info", e)
                null
            }

            // Detect network type
            val networkType = getNetworkType()

            // Get app version
            val appVersion = try {
                val pInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
                pInfo.versionName ?: "unknown"
            } catch (e: Exception) {
                "unknown"
            }

            // Send heartbeat
            apiService.sendHeartbeat(
                HeartbeatRequest(
                    userId = userId,
                    userName = userInfo?.name ?: "Unknown",
                    networkType = networkType,
                    appVersion = appVersion
                )
            )

            Log.d(TAG, "Heartbeat sent successfully (network: $networkType)")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send heartbeat", e)
            Result.retry()
        }
    }

    private fun getNetworkType(): String {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return "none"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "none"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobile"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
            else -> "other"
        }
    }
}
