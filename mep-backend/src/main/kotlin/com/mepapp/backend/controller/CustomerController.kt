package com.mepapp.backend.controller

import com.mepapp.backend.entity.Customer
import com.mepapp.backend.repository.CustomerRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasRole('ADMIN')")
class CustomerController(private val customerRepository: CustomerRepository) {

    @GetMapping
    fun getAll() = customerRepository.findAll()

    @PostMapping
    fun create(@RequestBody customer: Customer) = customerRepository.save(customer)

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: UUID) = customerRepository.findById(id)
}
