package com.jdbank.onboarding.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import com.jdbank.onboarding.entity.Seller
import com.jdbank.onboarding.entity.User
import com.jdbank.onboarding.repository.SellerRepository
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.transaction.Transactional
import kotlin.random.Random

/*
Upload file implemenation
1-> Gets xcel file as parameter
2-> Checks if the file is valid or not
3-> if good send the file to good data folder in s3, else send to bad data folder in s3
4-> Data in good files stored in Seller DB and Profile created in Security DB
 @author:Cyril Joseph
 */
@Service
class UploadFileImpl:UploadFile {

    private var bucketName: String = "jdbankstorage2"

    @Autowired
    private lateinit var s3Client: AmazonS3

    @Autowired
    private lateinit var sellerRepository: SellerRepository

    private lateinit var workBook: Workbook
    private var extensions = listOf<String>("xlsx", "csv")
    private var securityServicUrl =
       // "http://sellercertificationsecurity-env.eba-remvumx4.ap-south-1.elasticbeanstalk.com/api/auth/signupbulk"
        "http://localhost:5000/api/auth/signupbulk"
    /*
    @author:Cyril Joseph
    @parameter: file Multipart file containing seller information
    @checks: if file is having required extension
    @function: seperates good data and bad data folder -> adds good data to Seller and Security DB
    @returns: status of file
    */
    @Transactional
    override fun uploadDocument(file: MultipartFile): String {
        val document: File = convertMultipartFileToFile(file)
        val fileName: String? = file.originalFilename
        val lst: MutableList<String> = mutableListOf()

        if (!file.isEmpty) {
            try {
                //Add all incomming files to allfiles directory
                s3Client.putObject(PutObjectRequest(bucketName + "/allfiles", fileName, document))

                //Check if file is having required extensiion
                if (extensions.contains(fileName?.split(".")?.get(1))) {


                    var dataFormatter: DataFormatter = DataFormatter()
                    //Create a workbook
                    try {
                        workBook = WorkbookFactory.create(document)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    //Get sheet at 0 index
                    var sheet: Sheet = workBook.getSheetAt(0)

                    //Get number of columns in list
                    var noOfColumns = sheet.getRow(0).lastCellNum
                    println("----------Sheet has $noOfColumns columns")

                    //Iterate through rows and cols
                    for (row in sheet) {
                        for (cell in row) {
                            var cellValue: String = dataFormatter.formatCellValue(cell)
                            lst.add(cellValue)
                        }
                    }
                    //Filling excel data and creating list as List<Seller>
                    var sellerList: MutableList<Seller> = createList(lst, noOfColumns.toInt())
                    println(sellerList)
                    try {
                        createSellerProfile(sellerList) // save seller list to Security DB
                        sellerRepository.saveAll(sellerList) // save to Seller DB
                        s3Client.putObject(PutObjectRequest(bucketName + "/processedfiles", fileName, document))
                        //Move file to processedFile in s3 bucket
                        return "data saved in database , file procesed successfully and profile created"
                    } catch (e: Exception) {
                        s3Client.putObject(PutObjectRequest(bucketName + "/processedfiles", fileName, document))
                        e.printStackTrace()
                        return e.message.toString()
                    }


                } else {
                    try {
                        s3Client.putObject(PutObjectRequest(bucketName + "/errorfiles", fileName, document))
                        return "Some error in file";
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return e.message.toString()
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
                return e.message.toString()
            }
        } else {
            return "File not present"
        }
        // Rest template->  security service
    }



    /*
    @author: Cyril joseph
    @parameters: excelData->exceldata, noOfColumns
    @scope: helper function for uploadDocuments()
    @returns : Excel row data to List of seller objects
    * **/
    private fun createList(excelData: MutableList<String>, noOfColumns: Int): MutableList<Seller> {
        var sellerList: MutableList<Seller> = mutableListOf()
        var i: Int = noOfColumns
        do {
            var seller: Seller = Seller(
                aadharNumber = excelData[i],
                address = excelData[i + 1],
                age = excelData[i + 2].toInt(),
                certNo = excelData[i + 3],
                emailId = excelData[i + 4],
                name = excelData[i + 5],
                panNumber = excelData[i + 6],
                phNo = excelData[i + 7]
            )
            sellerList.add(seller)
            i += noOfColumns
        } while (i < excelData.size);
        return sellerList
    }

    /*
    * @author: Cyril Joseph
    * @params : file
    * @returns : Multipart file converted
    * */

    private fun convertMultipartFileToFile(file: MultipartFile): File {
        var convertedFile: File = File(file.originalFilename)
        try {
            var fos: FileOutputStream = FileOutputStream(convertedFile)
            fos.write(file.bytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return convertedFile;
    }

    /*
    @params : List containing seller objects
    @scope : called by function uploadDocuments() for creating seller profile
    @body: converts seller object into User object for
     automated profile creation and makes restTemplate call to security service
     */
    override fun createSellerProfile(sellerList:MutableList<Seller>) { //sellerList:MutableList<Seller>
        // convert MutableList<Seller> -> MutableList<User>
        val userList = sellerList.map {
                seller -> User( id = "L1-"+generateId(),
                                fullName= seller.name,
                                organization = "Jd bank",
                                username=seller.emailId,
                                mobile = seller.phNo,
                                password = "\$2a\$10\$jbIi/RIYNm5xAW9M7IaE5.WPw6BZgD8wcpkZUg0jm8RHPtdfDcMgm",
                                isAdmin=false
                )
        }

        // Make the rest template call
        val restTemplate:RestTemplate = RestTemplate()
        val headers:HttpHeaders = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity:HttpEntity<List<User>> = HttpEntity<List<User>>(userList, headers)
        val result = restTemplate.exchange(securityServicUrl, HttpMethod.POST,
                                                entity, String.javaClass)


    }
    /*
    @author:Cyril Joseph
    @params:None
    @returns:Random num generated with prefix 0's
    Generate random Id for seller
     */
    fun generateId():String{
        val num = Random.nextInt(1000).toString()
        return if(num.length==1)
                    "00$num"
                else if (num.length==2)
                    "0$num"
                else
                    num
    }
}