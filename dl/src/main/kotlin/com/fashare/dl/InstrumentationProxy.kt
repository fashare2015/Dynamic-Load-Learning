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
class InstrumentationProxy(val base: Instrumentation) : Instrumentation() {
    var mIntentProxy: IntentProxy? = null

    /**
     * new 之前，取出原始的 intent 来 new Activity实例
     */
    override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity? {
        logd("newActivity short: " + className)
        return (mIntentProxy?.base ?: intent)?.let {
            base.newActivity(DL.dexClassLoader, it.component?.className, it)
        }
    }

    /**
     * start 之前，替换 Intent 为 已注册的 StubActivity, 以绕过 系统对 manifest 的检查
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

    override fun callActivityOnCreate(activity: Activity?, icicle: Bundle?) {
        if (isFromPlugin(activity)) {
            val pluginRes = PluginResources(activity?.resources)
            try {
                Reflect.on(activity?.baseContext).set("mResources", pluginRes)
                Reflect.on(activity).set("mResources", pluginRes)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                Reflect.on(activity?.baseContext).field("mPackageInfo")   // get LoadedApk
                        .set("mClassLoader", DL.dexClassLoader)

                Reflect.on(pluginRes).set("mClassLoader", DL.dexClassLoader)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        base.callActivityOnCreate(activity, icicle)
    }

    private fun isFromPlugin(activity: Activity?): Boolean {
        return activity?.javaClass?.canonicalName?.startsWith("com.fashare.testapk") ?: false
    }
}
