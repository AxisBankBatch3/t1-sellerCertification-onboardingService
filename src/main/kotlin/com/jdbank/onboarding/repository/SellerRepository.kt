package com.jdbank.onboarding.repository



import com.jdbank.onboarding.entity.Seller
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SellerRepository : MongoRepository<Seller?, String?> {

    fun findByEmailId(emailId:String)

    @Query()
    fun existsSellerByEmailId(emailId:String)

}
