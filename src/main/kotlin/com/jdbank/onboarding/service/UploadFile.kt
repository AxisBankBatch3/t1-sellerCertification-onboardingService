package com.jdbank.onboarding.service

import com.jdbank.onboarding.entity.Seller
import org.springframework.web.multipart.MultipartFile


interface UploadFile {
    fun uploadDocument(file: MultipartFile): String?
    fun createSellerProfile(sellerList:MutableList<Seller>)
}
