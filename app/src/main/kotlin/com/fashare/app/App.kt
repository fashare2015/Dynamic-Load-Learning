package com.fashare.app

import android.app.Application
import android.net.Uri
import android.os.Environment
import com.fashare.dl.DL
import java.io.File

class App: Application(){
    override fun onCreate() {
        super.onCreate()

        DL.replaceInstrumentation()
        val pluginFile = File(Environment.getExternalStorageDirectory(), "testapk-with-res.apk")
        assets.open("testapk-with-res.apk").copyTo(pluginFile.outputStream())
        DL.loadApk(this, Uri.fromFile(pluginFile))
    }
}
