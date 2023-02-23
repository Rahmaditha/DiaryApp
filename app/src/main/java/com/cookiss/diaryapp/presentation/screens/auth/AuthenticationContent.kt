package com.cookiss.diaryapp.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookiss.diaryapp.R
import com.cookiss.diaryapp.presentation.components.GoogleButton
import com.cookiss.diaryapp.presentation.components.MessageBar
import com.stevdzasan.messagebar.MessageBarState

@Composable
fun AuthenticationContent(
    loadingState: Boolean,
    onButtonClicked: () -> Unit
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(9f)
                .fillMaxWidth()
                .padding(all = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(weight = 10f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.auth_title),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Text(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    text = stringResource(id = R.string.auth_subtitle),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
            Column(
                modifier = Modifier.weight(weight = 2f),
                verticalArrangement = Arrangement.Bottom
            ) {
                GoogleButton(
                    loadingState = loadingState,
                    onClick = onButtonClicked
                )
            }
        }
    }

//    Column(
////        Modifier.fillMaxWidth(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
////        Column(modifier = Modifier.weight(0.1f)) {
////            MessageBar(messageBarState = messageBarState)
////        }
//        Column(
//            modifier = Modifier
//                .weight(0.9f)
//                .fillMaxWidth()
//                .padding(all = 40.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            CentralContent(loadingState = loadingState, onButtonClicked = onButtonClicked)
//        }
//    }

}

@Composable
fun CentralContent(
    loadingState: Boolean,
    onButtonClicked: () -> Unit
) {
    Image(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .size(120.dp),
        painter = painterResource(id = R.drawable.google_logo),
        contentDescription = "google_logo"
    )
    Text(
        text = stringResource(R.string.auth_title),
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.headlineMedium.fontSize
    )
    Text(
        modifier = Modifier
            .alpha(ContentAlpha.medium)
            .padding(bottom = 40.dp, top = 4.dp),
        text = stringResource(R.string.auth_subtitle),
        fontSize = MaterialTheme.typography.bodySmall.fontSize,
        textAlign = TextAlign.Center
    )
    GoogleButton(
        loadingState = loadingState,
        onClick = onButtonClicked
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AuthenticationContentPreview() {
    AuthenticationContent(loadingState = false) {

    }
}

//Column(
//verticalArrangement = Arrangement.Center,
//horizontalAlignment = Alignment.CenterHorizontally
//) {
//    Column(
//        modifier = Modifier
//            .weight(9f)
//            .fillMaxWidth()
//            .padding(all = 40.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Column(
//            modifier = Modifier.weight(weight = 10f),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Image(
//                modifier = Modifier.size(120.dp),
//                painter = painterResource(id = R.drawable.google_logo),
//                contentDescription = "Google Logo"
//            )
//            Spacer(modifier = Modifier.height(20.dp))
//            Text(
//                text = stringResource(id = R.string.auth_title),
//                fontSize = MaterialTheme.typography.titleLarge.fontSize
//            )
//            Text(
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
//                text = stringResource(id = R.string.auth_subtitle),
//                fontSize = MaterialTheme.typography.bodyMedium.fontSize
//            )
//        }
//        Column(
//            modifier = Modifier.weight(weight = 2f),
//            verticalArrangement = Arrangement.Bottom
//        ) {
//            GoogleButton(
//                loadingState = loadingState,
//                onClick = onButtonClicked
//            )
//        }
//    }
//}