package com.cookiss.diaryapp.presentation.screens.home

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onMenuClicked:() -> Unit,
){
    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Hamburger Menu Icon",
                    tint = MaterialTheme.colorScheme.onSurface

                )
            }
        },
        title = {
            Text(
                text = "Diary",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date Icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}