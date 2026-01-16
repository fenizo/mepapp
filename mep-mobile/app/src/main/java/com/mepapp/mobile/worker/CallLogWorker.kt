package com.mepapp.mobile.worker

import android.content.Context
import android.provider.CallLog
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mepapp.mobile.network.CallLogRequest
import com.mepapp.mobile.network.MepApiService
import com.mepapp.mobile.network.NetworkModule
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class CallLogWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val apiService = NetworkModule.createService<MepApiService>()

    override suspend fun doWork(): Result {
        return try {
            // Ensure Auth Token is set
            val authRepository = com.mepapp.mobile.data.AuthRepository(applicationContext)
            // Use first() to get current value
            val token = authRepository.authToken.first()
            
            if (token.isNullOrBlank()) {
                Log.e("CallLogWorker", "No auth token found, aborting sync")
                return Result.failure()
            }
            NetworkModule.setAuthToken(token)

            val staffId = try {
                apiService.getMe().id
            } catch (e: Exception) {
                Log.e("CallLogWorker", "Failed to fetch staff ID", e)
                // If getMe fails, we can't log correctly. Abort or retry.
                // Using dummy ID is risky if backend rejects it or it messes up data.
                // Better to fail and retry later.
                return Result.retry() 
            }

            val callLogs = getCallLogs(staffId)
            if (callLogs.isNotEmpty()) {
                apiService.logCalls(callLogs)
                Log.d("CallLogWorker", "Uploaded ${callLogs.size} call logs")
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("CallLogWorker", "Error uploading call logs", e)
            Result.retry()
        }
    }

    private fun getCallLogs(staffId: String): List<CallLogRequest> {
        val list = mutableListOf<CallLogRequest>()
        val projection = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME
        )

        // Sync last 30 days of calls to ensure data is captured
        val selection = "${CallLog.Calls.DATE} > ?"
        val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        val selectionArgs = arrayOf(thirtyDaysAgo.toString())

        val cursor = applicationContext.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)

            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                val type = when (it.getInt(typeIndex)) {
                    CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                    CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                    CallLog.Calls.MISSED_TYPE -> "MISSED"
                    else -> "UNKNOWN"
                }
                val date = it.getLong(dateIndex)
                val duration = it.getLong(durationIndex)
                
                val isoDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date(date))
                val contactName = it.getString(nameIndex)

                list.add(
                    CallLogRequest(
                        phoneNumber = number,
                        callType = type,
                        duration = duration,
                        contactName = contactName,
                        timestamp = isoDate,
                        staffId = staffId
                    )
                )
            }
        }
        return list
    }
}
