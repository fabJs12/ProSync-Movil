package com.luna.prosync.data.remote

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthClient(
    private val context: Context
) {
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    // TODO: Replace with your actual Web Client ID from Google Cloud Console
    // It usually ends with .apps.googleusercontent.com
    private val webClientId = "YOUR_WEB_CLIENT_ID_HERE" 

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val username = credential.id

        return if (googleIdToken != null) {
            SignInResult(
                data = UserData(
                    userId = username,
                    username = credential.displayName,
                    profilePictureUrl = credential.profilePictureUri?.toString()
                ),
                errorMessage = null,
                idToken = googleIdToken
            )
        } else {
            SignInResult(
                data = null,
                errorMessage = "No Google ID Token found",
                idToken = null
            )
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
    val idToken: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)
