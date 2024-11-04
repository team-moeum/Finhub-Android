package com.fotcamp.finhub

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class FinhubApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.KAKAO_SDK_KEY)
    }
}