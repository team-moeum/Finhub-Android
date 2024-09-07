package com.fotcamp.finhub

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleLogin private constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: GoogleLogin = GoogleLogin()

        fun getInstance(): GoogleLogin {
            return INSTANCE
        }
    }

    private lateinit var options: GoogleSignInOptions
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var context: Context
    private lateinit var complete: (Intent?) -> Unit

    fun init(context: Context, launcher: ActivityResultLauncher<Intent>) {
        this.context = context
        this.launcher = launcher
        this.options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()
    }

    fun receiveResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            complete(result.data)
        } else {
            complete(null)
        }
    }

    fun login(complete: (Intent?) -> Unit) {
        val signIn = GoogleSignIn.getClient(context, options)
        launcher.launch(signIn.signInIntent)

        this.complete = complete
    }
}