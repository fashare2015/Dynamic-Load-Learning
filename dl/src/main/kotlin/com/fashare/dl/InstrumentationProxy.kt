package com.fashare.dl

import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import org.joor.Reflect
import java.io.File

/**
 * 占坑用 Activity
 */
internal class StubActivity : AppCompatActivity()


/**
 * Intent 代理类，替换 component.className 为 StubActivity
 */
internal class IntentProxy(var base: Intent?) : Intent() {
    init {
        component = ComponentName(base?.component?.packageName,
                StubActivity::class.java.name)
    }
}

/**
 * 插件专用的 Resources. 从插件apk中读取资源, 不再从宿主apk读取.
 */
internal class PluginResources(hostResources: Resources?)
    : Resources(createAssetManager(hostResources), hostResources?.displayMetrics, hostResources?.configuration) {

    companion object {
        /**
         * 调用 AssetManager.addAssetPath(pluginFile.path). 以便从插件中加载资源.
         */
        private fun createAssetManager(hostResources: Resources?): AssetManager {
            val assetManager = AssetManager::class.java.newInstance()
            val pluginFile = File(Environment.getExternalStorageDirectory(), "testapk-with-res.apk")
            Reflect.on(assetManager).call("addAssetPath", pluginFile.path)
            return assetManager
        }
    }
}

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
        if (!isFromPlugin(activity)) {
            base.callActivityOnCreate(activity, icicle)
            return
        }
        val pluginRes = PluginResources(activity?.resources)
        // hack 各处的 mClassLoader, 以便从插件读 R.java . 这步不做的话，默认从宿主读取，会找不到 R.xxx.xxx .
        try {
            // 替换 ContextImpl.getClassLoader() 为 DL.dexClassLoader
            Reflect.on(activity?.baseContext).field("mPackageInfo")   // get LoadedApk
                    .set("mClassLoader", DL.dexClassLoader)

            // 替换 PluginResources.mClassLoader 为 DL.dexClassLoader
            Reflect.on(pluginRes).set("mClassLoader", DL.dexClassLoader)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val pluginTheme = pluginRes.newTheme().apply {
            val pluginThemeId = Reflect.on(Resources::class.java)
                    .call("selectDefaultTheme", 0, Build.VERSION.SDK_INT)
                    .get<Int>()
            this.applyStyle(pluginThemeId, false)
        }

        // hack 各处的 mResources, mTheme, 以便从插件读资源文件
        try {
            // 替换 ContextImpl.mResources, mTheme
            Reflect.on(activity?.baseContext)
                    .set("mResources", pluginRes)
                    .set("mTheme", pluginTheme)
            // 替换 ContextThemeWrapper.mResources, mTheme
            Reflect.on(activity)
                    .set("mResources", pluginRes)
                    .set("mTheme", pluginTheme)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Activity 的 ActionBar 会使用宿主 Application 的 icon, 即宿主 apk 的 ic_launcher,
        // 由于插件不能访问宿主的资源, 会导致找不到 ic_launcher, 此时暂时设为 0, hack 掉.
        // 以后加上插件自己的 Application 时, 可以移除这部分 hack.
        try {
            Reflect.on(activity).field("mActivityInfo")
                    .field("applicationInfo")
                    .set("icon", 0)

        } catch (e: Exception) {
            e.printStackTrace()
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
