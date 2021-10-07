package com.jdbank.onboarding.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import com.jdbank.onboarding.entity.Seller
import com.jdbank.onboarding.repository.SellerRepository
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Service
class UploadFileImpl:UploadFile {

    private var bucketName:String = "jdbankstorage2"

    @Autowired
    private lateinit var s3Client:AmazonS3

    @Autowired
    private lateinit var sellerRepository:SellerRepository

    private lateinit var workBook:Workbook

    private var extensions = listOf<String>("xlsx", "csv")


    override fun uploadDocument(file: MultipartFile): String {
        var document:File = convertMultipartFileToFile(file)
        var fileName:String? = file.originalFilename
        var lst:MutableList<String> = mutableListOf()

        if (!file.isEmpty){
            try {
                //Add all incomming files to allfiles directory
                s3Client.putObject(PutObjectRequest(bucketName+"/allfiles", fileName, document))

                //Check if file is having required extensiion
                if ( extensions.contains(fileName?.split(".")?.get(1))){


                    var dataFormatter: DataFormatter = DataFormatter()
                //Create a workbook
                    try {
                        workBook = WorkbookFactory.create(document)
                    } catch (e:IOException){
                        e.printStackTrace()
                    }
                    println("--------Workbook has ${workBook.numberOfSheets} sheets -------------")

                    //Get sheet at 0 index
                    var sheet:Sheet = workBook.getSheetAt(0)

                    //Get number of columns in list
                    var noOfColumns = sheet.getRow(0).lastCellNum
                    println("----------Sheet has $noOfColumns columns")

                    //Iterate through rows and cols
                    for (row in sheet){
                        for (cell in row){
                            var cellValue:String = dataFormatter.formatCellValue(cell)
                            lst.add(cellValue)
                        }
                    }
                    //Filling excel data and creating list as List<Seller>
                    var sellerList:MutableList<Seller> =  createList(lst, noOfColumns.toInt())
                    try {
                        sellerRepository.saveAll(sellerList)
                        s3Client.putObject(PutObjectRequest(bucketName+"/processedfiles", fileName, document))
                        return "data saved in database and file procesed successfully"
                    } catch(e:Exception){
                        s3Client.putObject(PutObjectRequest(bucketName+"/processedfiles", fileName, document))
                        e.printStackTrace()
                        return e.message.toString()
                    }



                } else {
                    try {
                        s3Client.putObject(PutObjectRequest(bucketName+"/errorfiles", fileName, document))
                        return "Some error in file";
                    }catch (e:Exception){
                        e.printStackTrace()
                        return e.message.toString()
                    }

                }

            }catch (e:Exception){
                e.printStackTrace()
                return e.message.toString()
            }
        }else {
            return "File not present"
        }
    }

    private fun createList(excelData:MutableList<String> , noOfColumns:Int):MutableList<Seller>{
        var sellerList:MutableList<Seller> = mutableListOf()
        var i:Int = noOfColumns
        do {
            var seller:Seller = Seller(
                                        aadharNumber = excelData[i],
                                        address = excelData[i+1],
                                        age = excelData[i+2].toInt(),
                                        certNo = excelData[i+3],
                                        emailId = excelData[i+4],
                                        name = excelData[i+5],
                                        panNumber = excelData[i+6],
                                        phNo = excelData[i+7]
                                    )
            sellerList.add(seller)
            i += noOfColumns
        } while (i < excelData.size);
        return sellerList
    }

    fun convertMultipartFileToFile(file:MultipartFile):File{
        var convertedFile:File = File(file.originalFilename)
        try {
            var fos:FileOutputStream = FileOutputStream(convertedFile)
            fos.write(file.bytes)
        }catch (e:IOException){
            e.printStackTrace()
        }
        return convertedFile;
    }

}