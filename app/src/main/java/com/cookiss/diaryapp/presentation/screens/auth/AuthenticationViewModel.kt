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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _dialogAuthStateOpen = mutableStateOf(false)
    val dialogAuthStateOpen: State<Boolean> = _dialogAuthStateOpen

    private val _authenticated = mutableStateOf(false)
    val authenticated: State<Boolean> = _authenticated

    var loadingState = mutableStateOf(false)
        private set

//    private val _apiResponse: MutableState<RequestState<ApiResponse>> =
//        mutableStateOf(RequestState.Idle)
//    val apiResponse: State<RequestState<ApiResponse>> = _apiResponse

    init {
        viewModelScope.launch {
            authRepository.readSignedInState().collect { authenticated ->
                _authenticated.value = authenticated
            }
        }
    }

    fun setLoading(loading: Boolean) {
        loadingState.value = loading
    }

    fun saveAuthenticated(authenticated: Boolean) {
        viewModelScope.launch {
            authRepository.saveSignedInState(authenticated)
        }
    }

    fun setDialogAuthOpened(signedInState: Boolean) {
        viewModelScope.launch {
            _dialogAuthStateOpen.value = signedInState
        }
    }

    fun signInWithMongoAtlas(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ){
        viewModelScope.launch {
            try{
                val result = withContext(Dispatchers.IO){
                    App.create(APP_ID).login(
                        Credentials.jwt(tokenId)
//                        Credentials.google(tokenId, GoogleAuthType.ID_TOKEN)
                    ).loggedIn
                }
                withContext(Dispatchers.Main){
                    if(result){
                        onSuccess()
                        Log.d("viewModel", "signInWithMongoAtlas: success login")
                        delay(600)
                        _authenticated.value = true
                    }else{
                        Log.d("viewModel", "signInWithMongoAtlas: not success login")
                        onError(Exception("User is not logged in."))
                        delay(600)
                        _authenticated.value = false
                    }
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    onError(e)
                    Log.d("AuthViewModel", "signInWithMongoAtlas: error $e")
                }
            }
        }
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



