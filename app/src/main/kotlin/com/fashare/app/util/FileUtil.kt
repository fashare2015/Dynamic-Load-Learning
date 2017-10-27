package com.fashare.app.util

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtil{
    fun writeBytesToFile(inputStream: InputStream, file: File) {
        var fos: FileOutputStream? = null
        try {
            val data = ByteArray(2048)
            var nbread = inputStream.read(data)
            fos = FileOutputStream(file)
            while (nbread > -1) {
                fos.write(data, 0, nbread)
                nbread = inputStream.read(data)
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        } finally {
            if (fos != null) {
                fos.close()
            }
        }
    }

}
