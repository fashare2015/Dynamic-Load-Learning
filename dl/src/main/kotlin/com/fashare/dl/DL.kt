package com.fashare.dl

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
    @Throws(Exception:: class)
    fun loadDex(context: Context, uri: Uri){
        val dl = DexClassLoader(
                uri.path,
                context.cacheDir.path,
                null,
                context.classLoader)
        dl.loadClass("com.fashare.testapk.Test").apply {
            Reflect.on(this).call("sayHello", context)
        }
    }
}
