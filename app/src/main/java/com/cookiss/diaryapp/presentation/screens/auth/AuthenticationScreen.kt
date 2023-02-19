package com.cookiss.diaryapp.presentation.screens.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cookiss.diaryapp.util.Exceptions
//import com.stevdzasan.messagebar.MessageBarState
//import com.stevdzasan.onetap.OneTapSignInState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
//    loadingState: Boolean,
//    onButtonClicked: () -> Unit,
//    onSuccessfullFirebaseSignIn: (String) -> Unit,
//    onFailedFirebaseSignIn: (Exception) -> Unit,
//    navController: NavHostController,
    viewModel: AuthenticationViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
//    onTokenIdReceived: (String) -> Unit

){
    val activity = (LocalContext.current as Activity)
    val signedInState by viewModel.signedInState
    val messageBarState by viewModel.messageBarState

    Scaffold(
        content = {
//            ContentWithMessageBar(messageBarState = messageBarState) {
//                AuthenticationContent(
//                    loadingState,
//                    onButtonClicked
//                )
//            }

            AuthenticationContent(
                signedInState = signedInState,
                messageBarState = messageBarState,
                onButtonClicked = {
                    Log.d("AuthenticationScreen", "button clicked")
                    viewModel.saveSignedInState(signedInState = true)
                }
            )
        }
    )

    StartActivityForResult(
        key = signedInState,
        onResultReceived = { tokenId ->
            Log.d("AuthenticationScreen", "tokenId $tokenId")
            viewModel.signInWithMongoAtlas(
                tokenId = tokenId,
                onSuccess = {
                    if(it){
                        viewModel.setMessageBarState()
                        navigateToHome()
                    } },
                onError = {
                    viewModel.updateMessageBarState(it)
                }
            )
//            navigateToHome()

//            viewModel.verifyTokenOnBackend(ApiRequest(tokenId = tokenId))
        },
        onDialogDismissed = {
            viewModel.saveSignedInState(signedInState = false)
        },
    ) { activityLauncher ->
        Log.d("AuthenticationScreen", "$signedInState")
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