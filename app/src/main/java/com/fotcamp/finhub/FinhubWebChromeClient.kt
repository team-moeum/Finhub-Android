package com.fotcamp.finhub

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class FinhubWebChromeClient(
    private val onShowFilePicker: (Intent) -> Unit
) : WebChromeClient() {
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        this.filePathCallback = filePathCallback
        val filePickerIntent = fileChooserParams?.createIntent()
        filePickerIntent?.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        if (filePickerIntent == null) {
            cancelFileChooser()
        } else {
            onShowFilePicker(filePickerIntent)
        }
        return true
    }

    fun selectFiles(uris: Array<Uri>) {
        filePathCallback?.onReceiveValue(uris)
        filePathCallback = null
    }

    fun cancelFileChooser() {
        filePathCallback?.onReceiveValue(null)
        filePathCallback = null
    }
}