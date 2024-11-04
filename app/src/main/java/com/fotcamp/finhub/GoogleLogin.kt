package com.fotcamp.finhub

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


class GoogleLogin private constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: GoogleLogin = GoogleLogin()

        fun getInstance(): GoogleLogin {
            return INSTANCE
        }
    }

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var context: Context
    private lateinit var complete: (String?) -> Unit

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }

    fun init(context: Context, launcher: ActivityResultLauncher<Intent>) {
        this.context = context
        this.launcher = launcher
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(BuildConfig.GOOGLE_CLIENT_ID)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, googleSignInOption)
    }

    fun receiveResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)

                val serverAuth = account.serverAuthCode
                complete(serverAuth)
            } catch (e: ApiException) {
                Log.d("funhub", "Google API Exception : " + e.stackTraceToString())
            }
        } else {
            complete(null)
        }
    }

    fun login(complete: (String?) -> Unit) {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)

        this.complete = complete
    }
}