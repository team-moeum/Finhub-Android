package com.fotcamp.finhub

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

class FinhubRemoteConfig private constructor() {

    companion object {
        private var instance: FinhubRemoteConfig? = null
        private lateinit var remoteConfig: FirebaseRemoteConfig

        fun getInstance(): FinhubRemoteConfig {
            return instance ?: synchronized(this) {
                FinhubRemoteConfig().also {
                    remoteConfig = Firebase.remoteConfig
                }
            }
        }
    }

    fun ready() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 5
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun get(key: String, complete: (String) -> Unit?) {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                complete(remoteConfig.getString(key))
            }
        }
    }
}