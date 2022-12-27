package com.shrappz.gdgchennaigoodiedistrubutor.csv

import android.os.Environment
import com.shrappz.gdgchennaigoodiedistrubutor.model.CheckedInUser
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

class CSVHelperImpl @Inject constructor() : CSVHelper {

    override fun convertToCSVAndSave(fileName: String, list: List<CheckedInUser>): File {
        val csvFile =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/$fileName.csv")
        csvFile.createNewFile()
        val writer = FileWriter(csvFile)
        writer.write("CHECK_IN_ID\n")
        list.forEach {
            writer.write("${it.id}\n")
        }
        writer.close()
        return csvFile
    }
}