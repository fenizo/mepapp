package com.mepapp.backend.controller

import com.mepapp.backend.entity.DeviceStatus
import com.mepapp.backend.repository.DeviceStatusRepository
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.Duration
import java.util.*

data class HeartbeatRequest(
    val userId: String,
    val userName: String,
    val networkType: String = "unknown",
    val appVersion: String = "unknown"
)

data class DeviceStatusResponse(
    val userId: String,
    val userName: String,
    val online: Boolean,
    val networkType: String,
    val appVersion: String,
    val lastSeen: String,
    val lastSeenAgo: String
)

@RestController
@RequestMapping("/api/device")
class DeviceStatusController(
    private val deviceStatusRepository: DeviceStatusRepository
) {

    @PostMapping("/heartbeat")
    fun heartbeat(@RequestBody request: HeartbeatRequest): Map<String, String> {
        val userId = UUID.fromString(request.userId)
        val existing = deviceStatusRepository.findByUserId(userId)

        if (existing != null) {
            deviceStatusRepository.save(
                existing.copy(
                    lastSeen = Instant.now(),
                    networkType = request.networkType,
                    appVersion = request.appVersion,
                    userName = request.userName
                )
            )
        } else {
            deviceStatusRepository.save(
                DeviceStatus(
                    userId = userId,
                    userName = request.userName,
                    lastSeen = Instant.now(),
                    networkType = request.networkType,
                    appVersion = request.appVersion
                )
            )
        }

        return mapOf("status" to "ok")
    }

    @GetMapping("/status")
    fun getDeviceStatuses(): List<DeviceStatusResponse> {
        val allStatuses = deviceStatusRepository.findAll()
        val now = Instant.now()

        return allStatuses.map { status ->
            val duration = Duration.between(status.lastSeen, now)
            val online = duration.toMinutes() < 6 // Online if heartbeat within last 6 minutes

            val lastSeenAgo = when {
                duration.toMinutes() < 1 -> "Just now"
                duration.toMinutes() < 60 -> "${duration.toMinutes()} min ago"
                duration.toHours() < 24 -> "${duration.toHours()} hours ago"
                else -> "${duration.toDays()} days ago"
            }

            DeviceStatusResponse(
                userId = status.userId.toString(),
                userName = status.userName,
                online = online,
                networkType = status.networkType,
                appVersion = status.appVersion,
                lastSeen = status.lastSeen.toString(),
                lastSeenAgo = lastSeenAgo
            )
        }
    }
}
