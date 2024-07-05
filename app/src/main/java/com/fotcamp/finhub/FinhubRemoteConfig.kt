package com.fotcamp.finhub

import android.annotation.SuppressLint
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import org.json.JSONObject

class FinhubRemoteConfig private constructor() {
    companion object {
        private var instance: FinhubRemoteConfig? = null

        fun getInstance(): FinhubRemoteConfig {
            return instance ?: synchronized(this) {
                FinhubRemoteConfig().also {
                    it.remoteConfig = Firebase.remoteConfig
                }
            }
        }
    }

    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun ready() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 5
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun get(complete: (JSONObject) -> Unit?) {
        val json = JSONObject()

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                json.put("result", "success")
                json.put("config", getConfig())
            } else {
                val error = it.exception?.toString().let { "" }

                json.put("result", "error")
                json.put("resultMsg", error)
            }

            complete(json)
        }
    }

    private fun getConfig(): JSONObject {
        val values = JSONObject()
        val keys = remoteConfig.all.keys
        for (key in keys) {
            values.put(key, remoteConfig.getString(key))
        }

        return values
    }
}