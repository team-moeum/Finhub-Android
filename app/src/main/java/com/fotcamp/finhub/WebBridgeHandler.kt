package com.fotcamp.finhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

interface WebBridgeInterface {
    fun callbackWeb(callbackId: String, data: String?)
}

class WebBridgeHandler(private val context: Context, private val bridgeInterface: WebBridgeInterface?) {

    fun run(action: String, json: JSONObject) {
        this::class.members.firstOrNull { it.name == action }?.call(this, json)
    }

    fun share(json: JSONObject) {
        val urlString = json.getString("val2")

        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TITLE, urlString)

        context.startActivity(Intent.createChooser(intent, null))
    }

    fun appVersion(json: JSONObject) {
        val version: String = context.packageManager.getPackageInfo(context.packageName, 0).versionName

        val callback = json.getString("callbackId")
        if (callback.isNotEmpty()) {
            bridgeInterface?.callbackWeb(callback, version)
        }
    }

    fun getSafeAreaInset(json: JSONObject) {
        val activity = context as Activity
        val windowInsets = activity.window.decorView.rootWindowInsets
        val displayCutout = windowInsets.displayCutout

        val value = JSONObject()
        value.put("top", displayCutout?.safeInsetTop ?: 0)
        value.put("bottom", displayCutout?.safeInsetBottom ?: 0)
        value.put("left", displayCutout?.safeInsetLeft ?: 0)
        value.put("right", displayCutout?.safeInsetRight ?: 0)

        val callback = json.getString("callbackId")
        if (callback.isNotEmpty()) {
            bridgeInterface?.callbackWeb(callback, value.toString())
        }
    }

    fun setSafeAreaBackgroundColor(json: JSONObject) {

    }

    fun getPushToken(json: JSONObject) {
        val callback = json.getString("callbackId")
        if (callback.isEmpty()) {
            return
        }

        val tokenTask = FirebaseMessaging.getInstance().token

        if (tokenTask.isComplete) {
            bridgeInterface?.callbackWeb(callback, tokenTask.result)
        } else {
            tokenTask.addOnCompleteListener {
                bridgeInterface?.callbackWeb(callback, it.result)
            }
        }
    }

    fun getRemoteConfig(json: JSONObject) {
        val key = json.getString("val2")
        val callback = json.getString("callbackId")
        if (key.isEmpty() || callback.isEmpty()) {
            return
        }

        FinhubRemoteConfig.getInstance().get(key) {
            bridgeInterface?.callbackWeb(callback, it)
        }
    }
}