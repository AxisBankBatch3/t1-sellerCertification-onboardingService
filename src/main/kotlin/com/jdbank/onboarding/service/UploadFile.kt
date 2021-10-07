package com.jdbank.onboarding.service

import org.springframework.web.multipart.MultipartFile


interface UploadFile {
    fun uploadDocument(file: MultipartFile): String?
}
