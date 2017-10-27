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
class InstrumentationProxy(val base: Instrumentation): Instrumentation(){
    var mIntentProxy: IntentProxy? = null

    /**
     * new 之前，取出原始的 intent 来 new Activity实例
     */
    override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity? {
        logd("newActivity short: " + className)
        return (mIntentProxy?.base?: intent)?.let {
            base.newActivity(DL.dl, it.component?.className, it)
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
        } catch (e: Exception){
            loge("execStartActivity long: " + e.toString())
            null
        }
    }
}
