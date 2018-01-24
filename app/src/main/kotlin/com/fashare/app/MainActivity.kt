package com.fashare.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fashare.dl.DL
import com.fashare.dl.extension.loge
import com.fashare.dl.extension.toast
import org.joor.Reflect

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.btn_load_static_method).setOnClickListener {
            try {
                DL.dexClassLoader.loadClass("com.fashare.testapk.Test").apply {
                    Reflect.on(this).call("sayHello", it.context)
                }
            }catch (e: Exception){
                loge(e)
            }
        }

        findViewById(R.id.btn_goto_test_apk).setOnClickListener {
            try {
                DL.dexClassLoader.loadClass("com.fashare.testapk.PluginActivity").apply {

                    startActivity(Intent(this@MainActivity, this))
                }
            }catch (e: Exception){
                toast("加载失败")
                loge(e)
            }
        }
    }
}

