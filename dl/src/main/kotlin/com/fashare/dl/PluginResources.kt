package com.fashare.dl

import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Environment

import org.joor.Reflect

import java.io.File

/**
 * <pre>
 * author : jinliangshan
 * e-mail : jinliangshan@chexiang.com
 * desc   :
</pre> *
 */
class PluginResources(hostResources: Resources?) : Resources(createAssetManager(hostResources), hostResources?.displayMetrics, hostResources?.configuration) {
    companion object {
        private fun createAssetManager(hostResources: Resources?): AssetManager {
//            val assetManager = AssetManager::class.java.newInstance()
            val assetManager = hostResources?.assets!!
            val pluginFile = File(Environment.getExternalStorageDirectory(), "testapk-with-res.apk")
            Reflect.on(assetManager).call("addAssetPath", pluginFile.path)
            return assetManager
        }
    }
}
