package com.jdbank.onboarding.entity


import org.springframework.data.annotation.Id
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field
import javax.persistence.Entity


@Entity
@Document(collection = "invoice")
data class Seller(
    @Id
    var id:String = "" ,

    @Pattern(regexp="^\\d{4}\\s\\d{4}\\s\\d{4}$")
    @NotBlank(message = "Aadhar number is mandatory")
    var aadharNumber:String,

    @NotBlank(message = "address is mandatory")
    var address:String,

    @Min(18)
    @Max(60)
    var age:Int,

    @NotBlank(message = "Certification Number is mandatory")
    var certNo:String,

    @Email(message = "Enter A Valid Email Id !!!")
    @NotBlank(message = "Email id is mandatory")
    var emailId:String,

    @Pattern(regexp = "[A-Za-z ]+")
    @NotBlank(message = "Name is mandatory")
    var name:String,

    @Size(min=10, max=10)
    @Pattern(regexp="[A-Z]{5}[0-9]{4}[A-Z]{1}")
    @NotBlank(message = "Pan details is mandatory")
    var panNumber:String ,


    @Size(min=10, max=10)
    @NotBlank(message = "Phone number is mandatory")
    var phNo:String,

) {

}