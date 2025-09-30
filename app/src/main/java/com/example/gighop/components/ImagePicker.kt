package com.example.gighop.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.gighop.R
import java.io.File
import java.util.Objects

@Composable
fun ImagePicker(
    selectedImageUri: MutableState<Uri>,
) {

    var showDialog by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        file
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { isImageTaken ->
        if (isImageTaken)
            selectedImageUri.value = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if(it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
    }


    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { galleryUri ->
            if (galleryUri != null)
                selectedImageUri.value = galleryUri
        }
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.image_large)),
        contentAlignment = Alignment.Center
    ) {
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(
                                dimensionResource(id = R.dimen.padding_small)
                            )
                        )
                        .padding(dimensionResource(id = R.dimen.padding_large)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(id = R.string.choose_method))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            val permissionCheckResult =
                                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED)
                                cameraLauncher.launch(uri)
                            else
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                        }) {
                            Text(text = stringResource(id = R.string.camera))
                        }
                        Button(onClick = {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                            showDialog = false
                        }) {
                            Text(text = stringResource(id = R.string.gallery))
                        }
                    }
                }
            }
        }

        if (selectedImageUri.value == Uri.EMPTY) {
            Image(
                painter = painterResource(id = R.drawable.profile_image),
                contentDescription = stringResource(id = R.string.profile_image),
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.image_large))
                    .border(
                        BorderStroke(
                            dimensionResource(id = R.dimen.border_medium),
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        showDialog = true
                    }
            )
        } else {
            AsyncImage(
                model = selectedImageUri.value,
                contentDescription = stringResource(id = R.string.profile_image),
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.image_large))
                    .border(
                        BorderStroke(
                            dimensionResource(id = R.dimen.border_medium),
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        showDialog = true
                    },
                contentScale = ContentScale.Crop
            )
        }
    }
}

fun Context.createImageFile(): File {
    val timeStamp = System.currentTimeMillis()
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        externalCacheDir
    )
}