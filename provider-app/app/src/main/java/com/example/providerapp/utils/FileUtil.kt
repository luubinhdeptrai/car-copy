// file: utils/FileUtil.kt
package com.example.providerapp.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    fun uriToFile(context: Context, uri: Uri, fileName: String = "temp_image"): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            // Tạo một file tạm trong bộ nhớ cache của ứng dụng
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}