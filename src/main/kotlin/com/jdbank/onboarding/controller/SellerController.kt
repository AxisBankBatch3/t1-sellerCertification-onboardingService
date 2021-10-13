package com.jdbank.onboarding.controller

import com.jdbank.onboarding.entity.Seller
import com.jdbank.onboarding.service.OnboardingServiceSingleUser
import com.jdbank.onboarding.service.UploadFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.websocket.server.PathParam


/**
    put author information 
*/
@RestController
@CrossOrigin("*", maxAge = 3600)
class SellerController {

    @Autowired
    private lateinit var onboardingServiceSingleUser:OnboardingServiceSingleUser

    @Autowired
    private lateinit var uploadFile:UploadFile

    @GetMapping("/welcome")
    fun welcome():String{
        return "Welcome to onboarding services of Jbbank.com"
    }

    @GetMapping("/getAllSellers")
    fun getAllSellers(): MutableList<Seller?>? {
        return onboardingServiceSingleUser.getAllSellers()
    }

    @GetMapping("/getSellerByid/{id}")
    fun getSellerById(@PathVariable id:String):Any?{
        return onboardingServiceSingleUser.getSellerById(id)
    }


    @PostMapping("/uploadFile")
    fun uploadFile(@PathParam("file") file: MultipartFile):String?{
        return uploadFile.uploadDocument(file)
    }

    @PostMapping("/addInvoice")
    fun addSeller(@RequestBody seller:Seller):Seller?{
        return onboardingServiceSingleUser.addSeller(seller)
    }



}