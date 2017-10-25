package com.fashare.testapk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Hello, plugin Activity !!!", Toast.LENGTH_LONG).show()
        // TODO: 加载资源
//        setContentView(R.layout.activity_main)
    }
}
