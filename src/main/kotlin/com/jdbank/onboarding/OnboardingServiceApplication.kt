package com.jdbank.onboarding

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OnboardingServiceApplication

fun main(args: Array<String>) {
    runApplication<OnboardingServiceApplication>(*args)
    println("hello")
}