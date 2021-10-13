package com.jdbank.onboarding

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
@OpenAPIDefinition(info= Info(title = "Onboarding Service", version = "2.0",
                    description = "Onboarding service for Seller certification "))
open class OnboardingServiceApplication

fun main(args: Array<String>) {
    runApplication<OnboardingServiceApplication>(*args)
    println("hello")
}