package com.fashare.testapk

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Hello, plugin Activity !!!", Toast.LENGTH_LONG).show()
        setContentView(R.layout.activity_main)
    }
}
