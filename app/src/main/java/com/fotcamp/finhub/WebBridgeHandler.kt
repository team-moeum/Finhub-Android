package com.fotcamp.finhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, urlString)
        }

        context.startActivity(Intent.createChooser(intent, urlString))
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
        val callback = json.getString("callbackId")
        if (callback.isEmpty()) {
            return
        }

        FinhubRemoteConfig.getInstance().get() {
            bridgeInterface?.callbackWeb(callback, it.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getNotificationPermission(json: JSONObject) {
        val callback = json.getString("callbackId")
        if (callback.isEmpty()) {
            return
        }

        val granted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        val result = JSONObject().apply {
            put("result", granted == PackageManager.PERMISSION_GRANTED)
        }

        bridgeInterface?.callbackWeb(callback, result.toString())
    }

    fun requestNotificationPermission(json: JSONObject) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = arrayOf(android.Manifest.permission.POST_NOTIFICATIONS)
            (context as MainActivity).requestPermissions(permission, MainActivity.PERMISSION_REQUEST_CODE)
        } else {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        }
    }
}