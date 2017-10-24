package com.fashare.dl

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.net.Uri
import dalvik.system.DexClassLoader
import org.joor.Reflect

/**
 * <pre>
 *     author : jinliangshan
 *     e-mail : jinliangshan@chexiang.com
 *     desc   :
 * </pre>
 */
object DL{
    lateinit var dl: DexClassLoader

    fun init(context: Context, uri: Uri){
        dl = DexClassLoader(
                uri.path,
                context.cacheDir.path,
                null,
                context.classLoader)
    }

    fun replaceIntrumentation(activity: Activity){
        Reflect.on(activity).field("mMainThread").apply {
            val activityThread = this
            val base = activityThread.get<Instrumentation>("mInstrumentation")
            activityThread.set("mInstrumentation", InstrumentationProxy(base))
        }
    }
}
