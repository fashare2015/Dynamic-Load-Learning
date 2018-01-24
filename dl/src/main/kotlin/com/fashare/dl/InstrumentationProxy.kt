package com.fashare.dl

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.fashare.dl.extension.logd
import com.fashare.dl.extension.loge
import org.joor.Reflect

/**
 * Instrumentation 代理类
 */
internal class InstrumentationProxy(val base: Instrumentation) : Instrumentation() {
    var mIntentProxy: IntentProxy? = null

    /**
     * new 之前，取出原始的 intent 来 new Activity实例
     */
    override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity? {
        logd("newActivity short: " + className)
        return (mIntentProxy?.base ?: intent)?.let {
            mIntentProxy = null
            base.newActivity(DL.dexClassLoader, it.component?.className, it)
        }
    }

    /**
     * start 之前，替换 Intent 为 已注册的 StubActivity, 以绕过 系统对 manifest 的检查
     *
     * 注: 这里其实也是 override, 只是 super.execStartActivity(...) 为 @hide, 所以看起来比较奇怪.
     */
    fun execStartActivity(
            who: Context?, contextThread: IBinder?, token: IBinder?, target: Activity?,
            intent: Intent?, requestCode: Int, options: Bundle?): ActivityResult? {
        logd("execStartActivity long: " + intent?.component)

        mIntentProxy = IntentProxy(intent)

        return try {
            Reflect.on(base).call("execStartActivity",
                    who, contextThread, token, target,
                    mIntentProxy, requestCode, options).get<ActivityResult?>()
        } catch (e: Exception) {
            loge("execStartActivity long: " + e.toString())
            null
        }
    }

    /**
     * 这里主要处理资源的加载, 包括图片等资源文件以及 R.java
     */
    override fun callActivityOnCreate(activity: Activity?, icicle: Bundle?) {
        if (isFromPlugin(activity)) {
            val pluginRes = PluginResources(activity?.resources)
            // hack 各处的 mClassLoader, 以便从插件读 R.java. 这步不做的话，默认从宿主读取，会找不到资源.
            try {
                // 替换 ContextImpl.getClassLoader() 为 DL.dexClassLoader
                Reflect.on(activity?.baseContext).field("mPackageInfo")   // get LoadedApk
                        .set("mClassLoader", DL.dexClassLoader)

                // 替换 PluginResources.mClassLoader 为 DL.dexClassLoader
                Reflect.on(pluginRes).set("mClassLoader", DL.dexClassLoader)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            val pluginTheme = pluginRes.newTheme()
            pluginTheme.applyStyle(android.R.style.Theme_Light, false)

            // hack 各处的 mResources, 以便从插件读资源文件
            try {
                // 替换 ContextImpl.mResources 为 pluginRes
                Reflect.on(activity?.baseContext).set("mResources", pluginRes)
                        .set("mTheme", pluginTheme)
                // 替换 ContextThemeWrapper.mResources 为 pluginRes
                Reflect.on(activity).set("mResources", pluginRes)
                        .set("mTheme", pluginTheme)

            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        base.callActivityOnCreate(activity, icicle)
    }

    /**
     * 是插件 apk 中的 Activity
     */
    private fun isFromPlugin(activity: Activity?): Boolean {
        return activity?.javaClass?.canonicalName?.startsWith("com.fashare.testapk") ?: false
    }
}
