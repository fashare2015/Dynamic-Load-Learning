package com.fashare.dl

import android.app.Instrumentation
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import dalvik.system.DexClassLoader
import org.joor.Reflect

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


/**
 * 动态加载（门面类）
 */
object DL {
    lateinit var dexClassLoader: DexClassLoader

    /**
     * 加载 sdcard 上的 未安装的 apk
     */
    fun loadApk(context: Context, uri: Uri) {
        dexClassLoader = DexClassLoader(
                uri.path,
                context.cacheDir.path,
                null,
                context.classLoader)
    }

    /**
     * 替换 ActivityThread.mInstrumentation
     */
    fun replaceInstrumentation() {
        Reflect.on("android.app.ActivityThread")
                .call("currentActivityThread").apply {

            val base = get<Instrumentation>("mInstrumentation")
            val instrumentation = InstrumentationProxy(base)
            set("mInstrumentation", instrumentation)
        }
    }
}
