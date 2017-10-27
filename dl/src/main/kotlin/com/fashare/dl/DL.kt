package com.fashare.dl

import android.app.Instrumentation
import android.content.Context
import android.net.Uri
import dalvik.system.DexClassLoader
import org.joor.Reflect

/**
 * 动态加载（门面类）
 */
object DL{
    lateinit var dl: DexClassLoader
    lateinit var instrumentation: Instrumentation

    /**
     * 加载 sdcard 上的 未安装的 apk
     */
    fun loadApk(context: Context, uri: Uri){
        dl = DexClassLoader(
                uri.path,
                context.cacheDir.path,
                null,
                context.classLoader)
    }

    /**
     * 替换 ActivityThread.mInstrumentation
     */
    fun replaceInstrumentation(){
        Reflect.on("android.app.ActivityThread").call("currentActivityThread").apply {
            val activityThread = this
            val base = activityThread.get<Instrumentation>("mInstrumentation")
            instrumentation = InstrumentationProxy(base)
            activityThread.set("mInstrumentation", instrumentation)
        }
    }
}
