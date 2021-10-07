package com.jdbank.onboarding.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class StorageConfig {
    var accessKey:String = "AKIAYORZ4ILUPTIKKTHV"
    var accessSecret:String = "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"
    var region:String = "ap-south-1"
    @Bean
    fun s3Client(): AmazonS3 {
        val credentials: AWSCredentials = BasicAWSCredentials(accessKey, accessSecret)
        return AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(region).build()
    }
}