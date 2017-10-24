package com.fashare.app

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.fashare.app.util.FileUtil
import com.fashare.dl.DL
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val file = File(Environment.getExternalStorageDirectory(), "testapk.dex")

        try {
            FileUtil.writeBytesToFile(assets.open("testapk.dex"), file)
        }catch (e: Exception){
            Log.d("MainActivity", e.toString())
        }


        findViewById(R.id.btn_load_static_method).setOnClickListener {
            try {
                DL.loadDex(this, Uri.fromFile(file))
            }catch (e: Exception){
                Log.d("MainActivity", e.toString())
            }
        }
    }
}

