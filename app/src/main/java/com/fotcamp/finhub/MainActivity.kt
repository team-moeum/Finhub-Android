package com.fotcamp.finhub

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val PERMISSION_REQUEST_CODE = 5000
        const val BASE_URL = "https://main.fin-hub.co.kr/"
    }

    private lateinit var webView: WebView

    var backPressTime = 0L

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        webView.settings.run {
            val url = BASE_URL
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            webSettings.textZoom = 100;
            webSettings.domStorageEnabled = true

            val originalUserAgent = webSettings.userAgentString
            val modifiedUserAgent =
                originalUserAgent.replace(" wv", "").replace("Version/\\d+\\.\\d+".toRegex(), "")
            webSettings.userAgentString = modifiedUserAgent

            webView.webViewClient = WebViewClient()
            webView.webChromeClient = WebChromeClient()
            webView.overScrollMode = View.OVER_SCROLL_NEVER

            webView.addJavascriptInterface(WebViewContentController(webView), "Bridge")

            webView.loadUrl(url)
        }
        this.packageManager.getPackageInfo(this.packageName, 0)

        checkPermission()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d(TAG, token)
        })

        FinhubRemoteConfig.getInstance().ready()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        runPushAction(intent)
    }

    override fun onResume() {
        super.onResume()

        runPushAction(intent)
        intent.putExtra("push", false)
    }

    private fun runPushAction(intent: Intent?) {
        if (intent == null) {
            return
        }

        val view = intent.getStringExtra("view")
        if (!view.isNullOrEmpty()) {
            webView.loadUrl(BASE_URL + view)
        }

        val action = intent.getStringExtra("action")
        if (!action.isNullOrEmpty()) {
            webView.post {
                val jsCode = "window.dispatchEvent(new CustomEvent('pushAction', { detail: '$action' }));"
                webView.evaluateJavascript(jsCode) {
                    if (!it.equals("true")) {
                        Log.e("finhub", "Error dispatching event in WebView")
                    }
                }
            }
        }
    }

    // 뒤로가기 기능 구현
    override fun onBackPressed() {
        val myWebView: WebView = findViewById(R.id.webview)

        if(myWebView.canGoBack()){
            myWebView.goBack()
        }else{
            if (System.currentTimeMillis() - backPressTime >= 2000) {
                backPressTime = System.currentTimeMillis()
                Toast.makeText(this, "한번 더 누르면 앱을 종료할 수 있어요", Toast.LENGTH_SHORT).show()
            } else {
                super.onBackPressed()
            }
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

class WebViewContentController(private val webView: WebView): WebBridgeInterface {

    @JavascriptInterface
    fun jsToNative(data: String) {
        val json = JSONObject(data)
        val action = json.getString("val1")

        val handler = WebBridgeHandler(webView.context, this)
        handler.run(action, json)
    }

    override fun callbackWeb(callbackId: String, data: String?) {
        val dataString = data?.replace("'", "\\'") ?: ""
        val jsCode = "window.dispatchEvent(new CustomEvent('$callbackId', { detail: '$dataString' }));"

        webView.post {
            webView.evaluateJavascript(jsCode) {
                if (it.equals("true")) {
                    Log.d("finhub","Event dispatched successfully with data")
                } else {
                    Log.d("finhub", "Error dispatching event in WebView")
                }
            }
        }
    }
}