package com.jdbank.onboarding.service

import com.jdbank.onboarding.entity.Seller
import com.jdbank.onboarding.repository.SellerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
/*
@author:Yaswanth
contains all functions for CRUD operations in Seller resource
 */
@Service
class OnboardingServiceSingleUserImpl:OnboardingServiceSingleUser {

    @Autowired
    private lateinit var sellerRepository:SellerRepository

    override fun addSeller(seller: Seller): Seller? {
        return sellerRepository.save(seller)
    }

    override fun deleteSellerById(id: String): String {
        return if (sellerRepository.existsById(id)){
            sellerRepository.deleteById(id)
             "Entry with $id deleted"
        }else
            "Entry with $id not found"
    }
    /*
    @author: Yaswanth
    @params: None
    @returns : list of sellers
     */

    override fun getAllSellers(): MutableList<Seller?>? {
        return sellerRepository.findAll()
    }

    /*
    @author: Yashwanth
    @param: id of seller to get
    @returns: Status message
     */

    override fun getSellerById(id: String): Any? {
        return if (sellerRepository.existsById(id)){
            sellerRepository.findById(id)
        }else
            "Seller with $id not found"
    }

    /*
    @author: Yaswanth
    @params: id : id of seller to updatta, seller: Upated info
    @returns : Status of the operation
     */

    override fun updateSellerById(id: String, seller:Seller): Any? {
        return if (sellerRepository.existsById(id)) {
            var orgSeller: Seller = sellerRepository.findById(id).get()
            orgSeller.name = seller.name
            orgSeller.emailId = seller.emailId
            orgSeller.aadharNumber = seller.aadharNumber
            orgSeller.address = seller.address
            orgSeller.age = seller.age
            orgSeller.certNo = seller.certNo
            orgSeller.phNo = seller.phNo
            orgSeller.panNumber = seller.panNumber
            sellerRepository.save(orgSeller)
        } else
            "Seller with $id not found"
    }
}


