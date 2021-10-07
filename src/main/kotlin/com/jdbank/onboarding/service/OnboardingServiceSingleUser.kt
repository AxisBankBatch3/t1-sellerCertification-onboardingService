package com.jdbank.onboarding.service

import com.jdbank.onboarding.entity.Seller


interface OnboardingServiceSingleUser {
    fun addSeller(seller:Seller):Seller?
    fun deleteSellerById(id:String):String
    fun getAllSellers():MutableList<Seller?>?
    fun getSellerById(id:String):Any?
    fun updateSellerById(id:String, seller:Seller):Any?
}