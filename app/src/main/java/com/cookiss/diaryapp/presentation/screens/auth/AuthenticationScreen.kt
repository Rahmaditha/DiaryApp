package com.cookiss.diaryapp.presentation.screens.auth

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.cookiss.diaryapp.util.Constants.CLIENT_ID
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    loadingState: Boolean,
    onButtonClicked: () -> Unit,
    messageBarState: MessageBarState,
    onSuccessfullFirebaseSignIn: (String) -> Unit,
    onFailedFirebaseSignIn: (Exception) -> Unit,
    oneTapState: OneTapSignInState
){
    Scaffold(
        content = {
            ContentWithMessageBar(messageBarState = messageBarState) {
                AuthenticationContent(
                    loadingState,
                    onButtonClicked
                )
            }
        }
    )

    ActivityResultContracts.StartActivityForResult(
        key = signedInState,
        onResultReceived = { tokenId ->
            Log.d("LoginScreen", tokenId)
            viewModel.verifyTokenOnBackend(ApiRequest(tokenId = tokenId))
        },
        onDialogDismissed = {
            viewModel.saveSignedInState(signedInState = false)
        },
    ) { activityLauncher ->
        if (signedInState) {
            viewModel.signIn(
                activity = activity,
                launchActivityResult = { intentSenderRequest ->
                    activityLauncher.launch(intentSenderRequest)
                },
                accountNotFound = {
                    viewModel.updateMessageBarState(e = Exceptions.GoogleAccountNotFoundException())
                    viewModel.saveSignedInState(signedInState = false)
                }
            )
        }
    }

    LaunchedEffect(key1 = apiResponse) {
        when (apiResponse) {
            is RequestState.Success -> {
                val response = (apiResponse as RequestState.Success<ApiResponse>).data.success
                if (response) {
                    navController.navigateTo(Screen.Profile.route)
                } else {
                    viewModel.saveSignedInState(signedInState = false)
                    (apiResponse as RequestState.Success<ApiResponse>).data.error?.let {
                        viewModel.updateMessageBarState(e = it)
                    }
                }
            }
            else -> {
            }
        }
    }

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = CLIENT_ID,
        onTokenIdReceived = { tokenId ->
            Log.d("Auth", "tokenId: $tokenId")
            messageBarState.addSuccess("Successfully Authenticated!")
        },
        onDialogDismissed = { message ->
            Log.d("Auth", "message: $message")
            messageBarState.addError(Exception(message))
        }
    )
}