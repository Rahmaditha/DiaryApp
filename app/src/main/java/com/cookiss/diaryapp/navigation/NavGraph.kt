package com.cookiss.diaryapp.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cookiss.diaryapp.domain.model.GalleryImage
import com.cookiss.diaryapp.domain.model.Mood
import com.cookiss.diaryapp.presentation.components.DisplayAlertDialog
import com.cookiss.diaryapp.presentation.screens.auth.AuthenticationScreen
import com.cookiss.diaryapp.presentation.screens.auth.AuthenticationViewModel
import com.cookiss.diaryapp.presentation.screens.home.HomeScreen
import com.cookiss.diaryapp.presentation.screens.home.HomeViewModel
import com.cookiss.diaryapp.presentation.screens.write.WriteScreen
import com.cookiss.diaryapp.presentation.screens.write.WriteViewModel
import com.cookiss.diaryapp.util.Constants.APP_ID
import com.cookiss.diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.cookiss.diaryapp.domain.model.RequestState
import com.cookiss.diaryapp.domain.model.rememberGalleryState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.stevdzasan.messagebar.rememberMessageBarState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
){
    NavHost(
        startDestination = startDestination,
        navController = navController
    ){
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
        )
        homeRoute(
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            onDataLoaded = onDataLoaded,
            navigateToWriteWithArgs = {
                navController.navigate(Screen.Write.passDiaryId(diaryId = it))
            }
        )
        writeRoute(
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }
}

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
){
    composable(route = Screen.Authentication.route){
        val messageBarState = rememberMessageBarState()
        val viewModel: AuthenticationViewModel = hiltViewModel()

//        LaunchedEffect(key1 = Unit ){
//            onDataLoaded
//        }

        AuthenticationScreen(
            onSuccessfullFirebaseSignIn = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        viewModel.setDialogAuthOpened(false)
                        viewModel.setLoading(false)
//                viewModel.saveSignedInState(signedInState = true)
                        messageBarState.addSuccess("Successfully Authenticated!")
                    },
                    onError = {
                        viewModel.setDialogAuthOpened(false)
                        viewModel.setLoading(false)
                        messageBarState.addError(it)
//                    onFailedFirebaseSignIn(it)
                    },
                )

            },
            messageBarState = messageBarState,
            onFailedFirebaseSignIn = {
                viewModel.setLoading(false)
//                viewModel.saveSignedInState(signedInState = false)
                messageBarState.addError(it)
            },
            navigateToHome = navigateToHome,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeRoute(
    navigateToAuth: () -> Unit,
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    onDataLoaded: () -> Unit
){
    composable(route = Screen.Home.route){
        val scope = rememberCoroutineScope()
        val viewModel: AuthenticationViewModel = hiltViewModel()
        val messageBarState = rememberMessageBarState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        val homeViewModel: HomeViewModel = viewModel()
        val diaries by homeViewModel.diaries

        var signOutDialogOpened by remember {
            mutableStateOf(false)
        }
        
        LaunchedEffect(key1 = diaries){
            if(diaries !is RequestState.Loading){
                onDataLoaded()
            }
        }

        HomeScreen(
            diaries = diaries,
            drawerState = drawerState,
            onSignOutClicked = {
               signOutDialogOpened = true
            },
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            navigateToWrite = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs
        )

//        LaunchedEffect(key1 = Unit){
//            MongoDB.configureTheRealm()
//        }
        
        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to Sign Out from your Google Account?",
            dialogOpened = signOutDialogOpened,
            onDialogClosed = {
                signOutDialogOpened = false
             },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if(user != null){
                        drawerState.close()
                        user.logOut()
                        viewModel.saveAuthenticated(false)
                        messageBarState.addSuccess("Successfully Logout")
                        delay(600)
                        withContext(Dispatchers.Main) {
                            navigateToAuth()
                        }
                    }

               }
            }
        )

//       ContentWithMessageBar(messageBarState =  messageBarState) {
//           Column(
//               modifier = Modifier
//                   .fillMaxSize()
//                   .background(MaterialTheme.colorScheme.surface)
//                   .statusBarsPadding()
//                   .navigationBarsPadding(),
////               modifier = Modifier.fillMaxSize(),
//               verticalArrangement = Arrangement.Center,
//               horizontalAlignment = Alignment.CenterHorizontally
//           ) {
//               Button(onClick = {
//                   scope.launch(Dispatchers.IO) {
////                    viewModel.setMessageBarState()
//                       App.create(APP_ID).currentUser?.logOut()
//                       viewModel.saveAuthenticated(false)
//                       messageBarState.addSuccess("Successfully Logout")
//                       delay(600)
//                       withContext(Dispatchers.Main) {
//                           navigateToAuth()
//                       }
//                   }
//               }) {
//                   Text(text = "Logout")
//               }
//           }
//       }

    }
}

@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(
    onBackPressed: () -> Unit
){
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARGUMENT_KEY){
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ){
        val pagerState = rememberPagerState()
        val context = LocalContext.current
        val writeViewModel: WriteViewModel = hiltViewModel()
        val uiState = writeViewModel.uiState
        val galleryState = writeViewModel.galleryState
        val pageNumber by remember {
            derivedStateOf { pagerState.currentPage }
        }

        LaunchedEffect(key1 = uiState){
            Log.d("SelectedDiary", "${uiState.selectedDiaryId}")
        }
        
        WriteScreen(
            onTitleChanged = { writeViewModel.setTitle(title = it) },
            onDescriptionChanged = { writeViewModel.setDescription(description = it) },
            uiState = uiState,
            galleryState = galleryState,
            pagerState = pagerState,
            onDeleteConfirmed = { writeViewModel.deleteDiary(
                onSuccess = {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                },
                onError = {message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            ) },
            onDateTimeUpdated = { writeViewModel.updateDateTime(zonedDateTime = it) },
            moodName = { Mood.values()[pageNumber].name },
            onBackPressed = onBackPressed,
            onSaveClicked = {
                writeViewModel.upsertDiary(
                    diary = it.apply { mood = Mood.values()[pageNumber].name },
                    onSuccess = { onBackPressed() },
                    onError = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onImageSelect = {
                val type = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"

                Log.d("NavGraph", "writeRoute URI: $it")
                writeViewModel.addImage(
                    image = it,
                    imageType = type
                )

            },
            onImageDeleteClicked = {
                galleryState.removeImage(it)
            }
        )
    }
}