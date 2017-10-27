package com.fashare.dl

import android.content.ComponentName
import android.content.Intent
import android.support.v7.app.AppCompatActivity

/**
 * Intent 代理类，替换 component.className 为 StubActivity
 */
class IntentProxy(var base: Intent?): Intent(){
    init {
        component = ComponentName(base?.component?.packageName, StubActivity::class.java.name)
    }
}

/**
 * 占坑用 Activity
 */
class StubActivity : AppCompatActivity()
