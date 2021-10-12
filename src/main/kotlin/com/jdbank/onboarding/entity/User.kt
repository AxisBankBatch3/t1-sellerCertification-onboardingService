package com.jdbank.onboarding.entity

import org.springframework.data.annotation.Id

data class User (
    @Id
    var id: String="",
    var fullName: String,
    var organization: String,
    var username: String,
    var mobile: String,
    var password: String,
    var isAdmin:Boolean ) {
}
