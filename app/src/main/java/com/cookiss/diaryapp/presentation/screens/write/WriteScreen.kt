package com.cookiss.diaryapp.presentation.screens.write

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.cookiss.diaryapp.domain.model.Diary
import com.cookiss.diaryapp.domain.model.Mood
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun WriteScreen(
    uiState: UiState,
    moodName: () -> String,
    pagerState: PagerState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onBackPressed: () -> Unit
){
    LaunchedEffect(key1 = uiState.mood){
        pagerState.scrollToPage(Mood.valueOf(uiState.mood.name).ordinal)
    }

    Scaffold(
        topBar = {
             WriteTopBar(
                 selectedDiary = uiState.selectedDiary,
                 moodName = moodName,
                 onDeleteConfirmed = onDeleteConfirmed,
                 onBackPressed = onBackPressed)
        },
        content = {
            WriteContent(
                pagerState = pagerState,
                title = uiState.title,
                onTitleChanged = onTitleChanged,
                description = uiState.description,
                onDescriptionChanged = onDescriptionChanged,
                paddingValues = it
            )
        }
    )
}