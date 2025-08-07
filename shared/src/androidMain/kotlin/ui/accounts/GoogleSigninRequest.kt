package ui.accounts


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import model.sync.PullSyncManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.ApiUrl

private const val webClientId = "257942461839-fta2f7lbg6tcmuvm0ofq5fkct2d3ql5g.apps.googleusercontent.com"

class SignInManager : KoinComponent {
    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(webClientId)
        .build()

    val client: HttpClient by inject()
    val context: Context = getKoin().get(Application::class)

    private val syncManager: PullSyncManager by inject()

    @OptIn(DelicateCoroutinesApi::class)
    fun requestSignIn() {
        val authReq = AuthorizationRequest.Builder()
            .requestOfflineAccess(webClientId)
            .setRequestedScopes(
                listOf(
                    Scope(Scopes.OPEN_ID),
                    Scope(Scopes.EMAIL),
                    Scope(Scopes.PROFILE)
                )
            ).build()


        val credentialManager = CredentialManager.create(context)
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Job()).launch {
            try {
                //Sign in dialog box.
                 credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                // Get authCode and send to backend for session cookie.
                Identity.getAuthorizationClient(context).authorize(authReq)
                    .addOnFailureListener {
                        Log.e("TAG", "requestSignIn: Failed Auth ${it.message}")
                    }
                    .addOnSuccessListener {
                        if (it.serverAuthCode != null && it.serverAuthCode != "null") {
                            GlobalScope.launch {
                                // TODO : Ensure https for prod.
                                val r = client.post("http://${ApiUrl}:8080/session") {
                                    setBody(it.serverAuthCode)
                                }.status
                                Log.i("TAG", "requestSignIn: $r")
                                if(r == HttpStatusCode.OK) syncManager.sync(true)
                            }
                        }
                    }

            } catch (e: GetCredentialException) {
                Log.e("Tag", "requestSignIn: failure ${e.errorMessage} ")
            }
        }
    }
}