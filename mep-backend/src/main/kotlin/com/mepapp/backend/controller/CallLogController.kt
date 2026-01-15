package com.mepapp.backend.controller

import com.mepapp.backend.entity.CallLog
import com.mepapp.backend.repository.CallLogRepository
import com.mepapp.backend.repository.JobRepository
import com.mepapp.backend.repository.UserRepository
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/call-logs")
class CallLogController(
    private val callLogRepository: CallLogRepository,
    private val jobRepository: JobRepository,
    private val userRepository: UserRepository
) {
    @PostMapping
    fun logCall(@RequestBody request: CallLogRequest): CallLog {
        val job = jobRepository.findById(request.jobId).orElseThrow { Exception("Job not found") }
        val staff = userRepository.findById(request.staffId).orElseThrow { Exception("Staff not found") }
        
        val callLog = CallLog(job = job, staff = staff)
        return callLogRepository.save(callLog)
    }

    @GetMapping("/job/{jobId}")
    fun getLogsByJob(@PathVariable jobId: UUID): List<CallLog> =
        callLogRepository.findByJobIdOrderByTimestampDesc(jobId)
}

data class CallLogRequest(
    val jobId: UUID,
    val staffId: UUID
)
