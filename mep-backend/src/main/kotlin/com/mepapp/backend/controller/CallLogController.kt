package com.mepapp.backend.controller

import com.mepapp.backend.entity.CallLog
import com.mepapp.backend.repository.CallLogRepository
import com.mepapp.backend.repository.JobRepository
import com.mepapp.backend.repository.UserRepository
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/call-logs")
class CallLogController(
    private val callLogRepository: CallLogRepository,
    private val jobRepository: JobRepository,
    private val userRepository: UserRepository
) {
    @PostMapping
    fun logCall(@RequestBody request: CallLogRequest): CallLog {
        val staffUUID = UUID.fromString(request.staffId)
        val staff = userRepository.findById(staffUUID).orElseThrow { RuntimeException("Staff not found") }
        val job = request.jobId?.let { jobId -> 
            val jobUUID = UUID.fromString(jobId)
            jobRepository.findById(jobUUID).orElse(null) 
        }
        
        val callLog = CallLog(
            job = job,
            staff = staff,
            phoneNumber = request.phoneNumber,
            duration = request.duration,
            callType = request.callType,
            contactName = request.contactName,
            timestamp = request.timestamp ?: LocalDateTime.now()
        )
        return callLogRepository.save(callLog)
    }

    @PostMapping("/batch")
    fun logCalls(@RequestBody requests: List<CallLogRequest>): List<CallLog> {
        return requests.map { logCall(it) }
    }

    @GetMapping
    fun getAllLogs(): List<CallLog> = callLogRepository.findAllByOrderByTimestampDesc()

    @GetMapping("/job/{jobId}")
    fun getLogsByJob(@PathVariable jobId: UUID): List<CallLog> =
        callLogRepository.findByJobIdOrderByTimestampDesc(jobId)

    @GetMapping("/staff/{staffId}")
    fun getLogsByStaff(@PathVariable staffId: UUID): List<CallLog> =
        callLogRepository.findByStaffIdOrderByTimestampDesc(staffId)

    @GetMapping("/ping")
    fun ping(): Map<String, String> = mapOf("status" to "ok")

}

data class CallLogRequest(
    val jobId: String?,
    val staffId: String,  // Changed from UUID to String
    val phoneNumber: String,
    val duration: Long,
    val callType: String,
    val contactName: String? = null,
    val timestamp: LocalDateTime? = null
)
