package com.finhub.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
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

            webView.webViewClient = WebViewClient()
            webView.webChromeClient = WebChromeClient()

            webView.loadUrl(url)
        }
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
}