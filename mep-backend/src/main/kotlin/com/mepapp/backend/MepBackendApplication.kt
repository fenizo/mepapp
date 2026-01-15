package com.mepapp.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MepBackendApplication

fun main(args: Array<String>) {
	runApplication<MepBackendApplication>(*args)
}
