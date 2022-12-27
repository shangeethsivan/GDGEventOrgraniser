package com.shrappz.gdgchennaigoodiedistrubutor.csv

import com.shrappz.gdgchennaigoodiedistrubutor.model.CheckedInUser
import java.io.File

interface CSVHelper {
    fun convertToCSVAndSave(fileName: String, list: List<CheckedInUser>): File
}