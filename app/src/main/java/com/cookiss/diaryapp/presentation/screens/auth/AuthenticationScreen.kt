package com.cookiss.diaryapp.presentation.screens.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.cookiss.diaryapp.util.Exceptions
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState

//import com.stevdzasan.messagebar.MessageBarState
//import com.stevdzasan.onetap.OneTapSignInState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    onSuccessfullFirebaseSignIn: () -> Unit,
    messageBarState: MessageBarState,
    onFailedFirebaseSignIn: (Exception) -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
){
    val activity = (LocalContext.current as Activity)
    val dialogAuthStateOpen by viewModel.dialogAuthStateOpen
    val authenticated by viewModel.authenticated
    val loadingState by viewModel.loadingState
//    val messageBarStatee by viewModel.messageBarState

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        content = {
            ContentWithMessageBar(messageBarState = messageBarState) {
                AuthenticationContent(
                    loadingState = loadingState,
                    onButtonClicked = {
                        Log.d("AuthenticationScreen", "button clicked")
                        viewModel.setLoading(true)
                        viewModel.setDialogAuthOpened(true)
                    }
                )
            }
        }
    )


    StartActivityForResult(
        key = dialogAuthStateOpen,
        onResultReceived = { tokenId ->
            Log.d("AuthenticationScreen", "tokenId $tokenId")
            viewModel.signInWithMongoAtlas(
                tokenId = tokenId,
                onSuccess = {
                    viewModel.setDialogAuthOpened(false)
                    onSuccessfullFirebaseSignIn()
                },
                onError = {
                    viewModel.setDialogAuthOpened(false)
                    onFailedFirebaseSignIn(it)
                },
            )

//            viewModel.verifyTokenOnBackend(ApiRequest(tokenId = tokenId))
        },
        onDialogDismissed = {
            viewModel.setDialogAuthOpened(false)
            viewModel.saveAuthenticated(false)
            viewModel.setLoading(false)
        },
    ) { activityLauncher ->
        Log.d("AuthenticationScreen", "$dialogAuthStateOpen")
        if (dialogAuthStateOpen) {
            viewModel.signIn(
                activity = activity,
                launchActivityResult = { intentSenderRequest ->
                    activityLauncher.launch(intentSenderRequest)
                },
                accountNotFound = {
                    messageBarState.addError(Exceptions.GoogleAccountNotFoundException())
                    viewModel.setLoading(false)
                    viewModel.setDialogAuthOpened(false)
                    viewModel.saveAuthenticated(false)
                }
            )
        }
    }

    LaunchedEffect(key1 = authenticated) {
        if (authenticated) {
            navigateToHome()
        }
    }

//    LaunchedEffect(key1 = apiResponse) {
//        when (apiResponse) {
//            is RequestState.Success -> {
//                val response = (apiResponse as RequestState.Success<ApiResponse>).data.success
//                if (response) {
//                    navController.navigateTo(Screen.Profile.route)
//                } else {
//                    viewModel.saveSignedInState(signedInState = false)
//                    (apiResponse as RequestState.Success<ApiResponse>).data.error?.let {
//                        viewModel.updateMessageBarState(e = it)
//                    }
//                }
//            }
//            else -> {
//            }
//        }
//    }

//    OneTapSignInWithGoogle(
//        state = oneTapState,
//        clientId = CLIENT_ID,
//        onTokenIdReceived = { tokenId ->
//            Log.d("Auth", "tokenId: $tokenId")
//            messageBarState.addSuccess("Successfully Authenticated!")
//        },
//        onDialogDismissed = { message ->
//            Log.d("Auth", "message: $message")
//            messageBarState.addError(Exception(message))
//        }
//    )


}