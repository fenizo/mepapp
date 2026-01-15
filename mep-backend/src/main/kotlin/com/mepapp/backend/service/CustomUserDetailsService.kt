package com.mepapp.backend.service

import com.mepapp.backend.entity.User
import com.mepapp.backend.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(phone: String): UserDetails {
        val user = userRepository.findByPhone(phone)
            ?: throw UsernameNotFoundException("User not found with phone: $phone")

        return org.springframework.security.core.userdetails.User(
            user.phone,
            user.passwordHash,
            listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        )
    }
}
