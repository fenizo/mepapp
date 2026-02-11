package com.mepapp.backend.repository

import com.mepapp.backend.entity.DeviceStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DeviceStatusRepository : JpaRepository<DeviceStatus, UUID> {
    fun findByUserId(userId: UUID): DeviceStatus?
}
