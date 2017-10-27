package com.fashare.app

import android.app.Application
import android.net.Uri
import android.os.Environment
import com.fashare.app.util.FileUtil
import com.fashare.dl.DL
import com.fashare.dl.extension.loge
import java.io.File

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        DL.replaceInstrumentation()

        val file = File(Environment.getExternalStorageDirectory(), "testapk-no-res.apk")
        try {
            FileUtil.writeBytesToFile(assets.open("testapk-no-res.apk"), file)
        }catch (e: Exception){
            loge(e)
        }
        DL.loadApk(this, Uri.fromFile(file))
    }
}
