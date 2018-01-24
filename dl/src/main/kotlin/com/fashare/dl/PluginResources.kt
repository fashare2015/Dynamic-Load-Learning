package com.fashare.dl

import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Environment

import org.joor.Reflect

import java.io.File

/**
 * 插件专用的 Resources. 从插件apk中读取资源, 不再从宿主apk读取.
 */
internal class PluginResources(hostResources: Resources?) : Resources(createAssetManager(hostResources), hostResources?.displayMetrics, hostResources?.configuration) {
    companion object {
        /**
         * 调用 AssetManager.addAssetPath(pluginFile.path). 以便从插件中加载资源.
         */
        private fun createAssetManager(hostResources: Resources?): AssetManager {
            val assetManager = AssetManager::class.java.newInstance()
//            val assetManager = hostResources?.assets!!
            val pluginFile = File(Environment.getExternalStorageDirectory(), "testapk-with-res.apk")
            Reflect.on(assetManager).call("addAssetPath", pluginFile.path)
            return assetManager
        }
    }
}
