package com.fashare.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.fashare.app.util.FileUtil
import com.fashare.dl.DL
import com.fashare.dl.extension.loge
import com.fashare.dl.extension.toast
import org.joor.Reflect
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val file = File(Environment.getExternalStorageDirectory(), "testapk.dex")
        try {
            FileUtil.writeBytesToFile(assets.open("testapk.dex"), file)
        }catch (e: Exception){
            loge(e)
        }
        DL.init(this, Uri.fromFile(file))
        DL.replaceIntrumentation(this)

        findViewById(R.id.btn_load_static_method).setOnClickListener {
            try {
                DL.dl.loadClass("com.fashare.testapk.Test").apply {
                    Reflect.on(this).call("sayHello", it.context)
                }
            }catch (e: Exception){
                loge(e)
            }
        }

        findViewById(R.id.btn_goto_second).setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))

//            try {
//                DL.dl.loadClass("com.fashare.testapk.MainActivity").apply {
//                    toast(this.canonicalName)
//                    startActivity(Intent(this@MainActivity, this))
//                }
//            }catch (e: Exception){
//                loge(e)
//            }
        }

        findViewById(R.id.btn_load_activity).setOnClickListener {
            try {
                DL.dl.loadClass("com.fashare.testapk.MainActivity").apply {
                    toast(this.canonicalName)
                }
            }catch (e: Exception){
                loge(e)
            }
        }
    }
}

