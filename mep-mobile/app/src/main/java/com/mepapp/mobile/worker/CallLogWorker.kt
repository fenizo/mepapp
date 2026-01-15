package com.mepapp.mobile.worker

import android.content.Context
import android.provider.CallLog
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mepapp.mobile.network.CallLogRequest
import com.mepapp.mobile.network.MepApiService
import com.mepapp.mobile.network.NetworkModule
import java.text.SimpleDateFormat
import java.util.*

class CallLogWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val apiService = NetworkModule.createService<MepApiService>()

    override suspend fun doWork(): Result {
        return try {
            val callLogs = getCallLogs()
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

    private fun getCallLogs(): List<CallLogRequest> {
        val list = mutableListOf<CallLogRequest>()
        val projection = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )

        // Only sync last 1 hour of calls to avoid heavy payload
        val selection = "${CallLog.Calls.DATE} > ?"
        val selectionArgs = arrayOf((System.currentTimeMillis() - 3600000).toString())

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

                // Get current user if not already fetched
                val staffId = try {
                    apiService.getMe().id
                } catch (e: Exception) {
                    "00000000-0000-0000-0000-000000000000"
                }

                list.add(
                    CallLogRequest(
                        phoneNumber = number,
                        callType = type,
                        duration = duration,
                        timestamp = isoDate,
                        staffId = staffId
                    )
                )
            }
        }
        return list
    }
}
