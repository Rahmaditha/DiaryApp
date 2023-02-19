package com.cookiss.diaryapp.presentation.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookiss.diaryapp.domain.model.MessageBarState
import com.cookiss.diaryapp.domain.repository.AuthRepository
import com.cookiss.diaryapp.util.Constants.APP_ID
import com.cookiss.diaryapp.util.oneTapSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signedInState = mutableStateOf(false)
    val signedInState: State<Boolean> = _signedInState

    private val _messageBarState = mutableStateOf(MessageBarState())
    val messageBarState: State<MessageBarState> = _messageBarState

//    private val _apiResponse: MutableState<RequestState<ApiResponse>> =
//        mutableStateOf(RequestState.Idle)
//    val apiResponse: State<RequestState<ApiResponse>> = _apiResponse

    init {
        viewModelScope.launch {
            authRepository.readSignedInState().collect { signedInState ->
                _signedInState.value = signedInState
            }
        }
    }

    fun saveSignedInState(signedInState: Boolean) {
        viewModelScope.launch {
            authRepository.saveSignedInState(signedInState)
        }
    }

    fun signInWithMongoAtlas(
        tokenId: String,
        onSuccess: (Boolean) -> Unit,
        onError: (Exception) -> Unit
    ){
        viewModelScope.launch {
            try{
                val result = withContext(Dispatchers.IO){
                    App.create(APP_ID).login(
                        Credentials.google(tokenId, GoogleAuthType.ID_TOKEN)
                    ).loggedIn
                }
                withContext(Dispatchers.Main){
                    Log.d("AuthViewModel", "tokenId: $tokenId")
                    Log.d("AuthViewModel", "signInWithMongoAtlas: $result")
                    onSuccess(result)
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    onError(e)
                    Log.d("AuthViewModel", "signInWithMongoAtlas: error $e")
                }
            }
        }
    }

    fun updateMessageBarState(e: Exception) {
        _messageBarState.value =
            MessageBarState(error = e)
    }

    fun setMessageBarState() {
        _messageBarState.value = MessageBarState(
            message = "Successfully Authenticated!",
        )
    }

    fun signIn(
        activity: Activity,
        launchActivityResult: (IntentSenderRequest) -> Unit,
        accountNotFound: (Exception) -> Unit
    ) {
        oneTapSignIn(
            activity = activity,
            launchActivityResult = launchActivityResult,
            setFilterByAuthorizedAccounts = true,
            setAutoSelectEnabled = true,
            addOnFailure = {
                // It means that not able to find the proper Google account on the smartphone.
                // So signUp is needed:
                Log.d("SignIn", "Signing up...")
                signUp(
                    activity = activity,
                    launchActivityResult = launchActivityResult,
                    accountNotFound = accountNotFound
                )
            }
        )
    }

    private fun signUp(
        activity: Activity,
        launchActivityResult: (IntentSenderRequest) -> Unit,
        accountNotFound: (Exception) -> Unit
    ) {
        oneTapSignIn(
            activity = activity,
            launchActivityResult = launchActivityResult,
            setFilterByAuthorizedAccounts = false,
            setAutoSelectEnabled = false,
            addOnFailure = { exception ->
                // It means that not there is no Google account on the smartphone (very low possibility).
                Log.d("SignUp", exception.message.toString())
                accountNotFound(exception)
            }
        )
    }


//    fun verifyTokenOnBackend(request: ApiRequest) {
//        _apiResponse.value = RequestState.Loading
//        try {
//            viewModelScope.launch {
//                val response = userRepository.verifyUserOnBackend(request)
//                _apiResponse.value = RequestState.Success(data = response)
//                _messageBarState.value = MessageBarState(
//                    message = response.message,
//                )
//            }
//        } catch (e: Exception) {
//            _apiResponse.value = RequestState.Error(t = e)
//            _messageBarState.value = MessageBarState(
//                error = e,
//            )
//        }
//    }
}



