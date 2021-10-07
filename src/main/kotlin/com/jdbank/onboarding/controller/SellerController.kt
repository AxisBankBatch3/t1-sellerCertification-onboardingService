package com.jdbank.onboarding.controller

import com.jdbank.onboarding.entity.Seller
import com.jdbank.onboarding.service.OnboardingServiceSingleUser
import com.jdbank.onboarding.service.UploadFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.websocket.server.PathParam

@RestController
@CrossOrigin("*", maxAge = 3600)
class SellerController {

    @Autowired
    private lateinit var onboardingServiceSingleUser:OnboardingServiceSingleUser

    @Autowired
    private lateinit var uploadFile:UploadFile

    @GetMapping("/welcome")
    fun welcome():String{
        return "Welcome to onboarding services of single user"
    }

    @GetMapping("/getAllSellers")
    fun getAllSellers(): MutableList<Seller?>? {
        return onboardingServiceSingleUser.getAllSellers()
    }

    @GetMapping("/getSellerByEmail/{id}")
    fun getSellerById(@PathVariable id:String):Any?{
        return onboardingServiceSingleUser.getSellerById(id)
    }

    @PostMapping("/uploadFile")
    fun uploadFile(@PathParam("file") file: MultipartFile):String?{
        return uploadFile.uploadDocument(file)
    }


}