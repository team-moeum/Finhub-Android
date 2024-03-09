package com.fotcamp.finhub

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val PERMISSION_REQUEST_CODE = 5000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.webview)
        webView.settings.run {
            val url = "https://finhub-front-end.vercel.app"
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            webSettings.textZoom = 100;

            webView.webViewClient = WebViewClient()
            webView.webChromeClient = WebChromeClient()

            webView.loadUrl(url)
        }

        checkPermission()
    }

    // 뒤로가기 기능 구현
    override fun onBackPressed() {
        val myWebView: WebView = findViewById(R.id.webview)

        if(myWebView.canGoBack()){
            myWebView.goBack()
        }else{
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults.first() != PERMISSION_GRANTED) {
                    // 알림 허용

                }
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)

            if (permission != PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            }
        }
    }
}