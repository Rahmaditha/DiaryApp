package com.cookiss.diaryapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cookiss.diaryapp.presentation.screens.auth.AuthenticationScreen
import com.cookiss.diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY

@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController){
    NavHost(
        startDestination = startDestination,
        navController = navController
    ){
        authenticationRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
//            onDataLoaded = onDataLoaded
        )
        homeRoute()
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit
){
    composable(route = Screen.Authentication.route){
        AuthenticationScreen(
            navigateToHome = navigateToHome,
        )
    }
}

fun NavGraphBuilder.homeRoute(){
    composable(route = Screen.Home.route){

    }
}

fun NavGraphBuilder.writeRoute(){
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARGUMENT_KEY){
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ){

    }
}