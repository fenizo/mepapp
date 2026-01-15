package com.mepapp.backend.controller

import com.mepapp.backend.entity.Role
import com.mepapp.backend.repository.UserRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasRole('ADMIN')")
class StaffController(private val userRepository: UserRepository) {

    @GetMapping
    fun getAllStaff() = userRepository.findAll().filter { it.role == Role.STAFF }

    @GetMapping("/all")
    fun getAllUsers() = userRepository.findAll()

    @DeleteMapping("/{id}")
    fun deleteStaff(@PathVariable id: UUID) = userRepository.deleteById(id)
}
