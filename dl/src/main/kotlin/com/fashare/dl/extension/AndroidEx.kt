package com.fashare.dl.extension

import android.content.Context
import android.util.Log
import android.widget.Toast
/**
 * <pre>
 *     author : jinliangshan
 *     e-mail : jinliangshan@chexiang.com
 *     desc   :
 * </pre>
 */
fun Context.toast(msg: String?){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Any.logd(msg: Any?){
    Log.d(this.javaClass.simpleName, msg.toString())
}

fun Any.loge(msg: Any?){
    Log.e(this.javaClass.simpleName, msg.toString())
}
