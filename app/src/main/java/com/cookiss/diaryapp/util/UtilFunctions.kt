package com.cookiss.diaryapp.util

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import io.realm.kotlin.types.RealmInstant
import java.time.Instant

/**
 * Download images from Firebase asynchronously.
 * This function returns imageUri after each successful download.
 * */
fun fetchImagesFromFirebase(
    remoteImagePaths: List<String>,
    onImageDownload: (Uri) -> Unit,
    onImageDownloadFailed: (Exception) -> Unit = {},
    onReadyToDisplay: () -> Unit = {}
) {
    if (remoteImagePaths.isNotEmpty()) {
        remoteImagePaths.forEachIndexed { index, remoteImagePath ->
            if (remoteImagePath.trim().isNotEmpty()) {
                FirebaseStorage.getInstance().reference.child(remoteImagePath.trim()).downloadUrl
                    .addOnSuccessListener {
                        Log.d("DownloadURL", "$it")
                        onImageDownload(it)
                        if (remoteImagePaths.lastIndexOf(remoteImagePaths.last()) == index) {
                            onReadyToDisplay()
                        }
                    }.addOnFailureListener {
                        onImageDownloadFailed(it)
                    }
            }
        }
    }
}

//fun retryUploadingImageToFirebase(
//    imageToUpload: ImageToUpload,
//    onSuccess: () -> Unit
//) {
//    val storage = FirebaseStorage.getInstance().reference
//    storage.child(imageToUpload.remoteImagePath).putFile(
//        imageToUpload.imageUri.toUri(),
//        storageMetadata { },
//        imageToUpload.sessionUri.toUri()
//    ).addOnSuccessListener { onSuccess() }
//}
//
//fun retryDeletingImageFromFirebase(
//    imageToDelete: ImageToDelete,
//    onSuccess: () -> Unit
//) {
//    val storage = FirebaseStorage.getInstance().reference
//    storage.child(imageToDelete.remoteImagePath).delete()
//        .addOnSuccessListener { onSuccess() }
//}

@RequiresApi(Build.VERSION_CODES.O)
fun RealmInstant.toInstant(): Instant {
    val sec: Long = this.epochSeconds
    val nano: Int = this.nanosecondsOfSecond
    return if (sec >= 0) {
        Instant.ofEpochSecond(sec, nano.toLong())
    } else {
        Instant.ofEpochSecond(sec - 1, 1_000_000 + nano.toLong())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Instant.toRealmInstant(): RealmInstant {
    val sec: Long = this.epochSecond
    val nano: Int = this.nano
    return if (sec >= 0) {
        RealmInstant.from(sec, nano)
    } else {
        RealmInstant.from(sec + 1, -1_000_000 + nano)
    }
}

fun oneTapSignIn(
    activity: Activity,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    setFilterByAuthorizedAccounts: Boolean,
    setAutoSelectEnabled: Boolean,
    addOnFailure: (Exception) -> Unit
) {
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(Constants.CLIENT_ID)
                .setFilterByAuthorizedAccounts(setFilterByAuthorizedAccounts)
                .build()
        )
        .setAutoSelectEnabled(setAutoSelectEnabled)
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(
                        result.pendingIntent.intentSender
                    ).build()
                )
            } catch (e: Exception) {
                Log.d("OneTapSignIn", "Couldn't start One Tap UI: ${e.message}")
            }
        }
        .addOnFailureListener { exception ->
            addOnFailure(exception)
        }
}
