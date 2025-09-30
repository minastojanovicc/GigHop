package com.example.gighop.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gighop.model.MapObject
import com.example.gighop.viewmodel.ObjectViewModel
import com.example.gighop.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectDetailsScreen(navController: NavHostController, objectViewModel: ObjectViewModel, userViewModel: UserViewModel, objectId: String) {
    var mapObject by remember { mutableStateOf<MapObject?>(null) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    var rating by remember { mutableStateOf(0f) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var ratingResult by remember { mutableStateOf<String?>(null) }
    var buttonText by remember { mutableStateOf("Leave a Rating") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mapObject) {
        if (mapObject == null) {
            objectViewModel.getObjectById(objectId) { mapObjectResult ->
                mapObject = mapObjectResult
                mapObjectResult?.let {
                    userViewModel.getUserRatingForObject(it.id) { userRating ->
                        if (userRating != null) {
                            rating = userRating.toFloat()
                            buttonText = "Update your rate"
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = mapObject?.title ?: "Music Band",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 16.dp),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (mapObject != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                val date = Date(mapObject!!.timestamp)
                val format = SimpleDateFormat("dd.MM.yyyy   HH:mm", Locale.getDefault())
                val datestring=format.format(date)

                mapObject?.photoUrl?.let { url ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)  // nice fade-in animation
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = mapObject?.title ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Place:  ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = mapObject?.subject ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Type:  ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = mapObject?.type ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Description:  ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = mapObject?.description ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp
                        )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Author:  ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = mapObject?.author ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Date:  ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = datestring.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Average Rating: ${mapObject?.rating?.let { String.format("%.2f", it) } ?: "No ratings yet"}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // If the current user is (not) an object owner
                if (mapObject?.ownerId != currentUser?.uid) {
                    Button(
                        onClick = {
                            showRatingDialog = true
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(buttonText, fontSize = 16.sp)
                    }
                } else {
                    Text(
                        text = "You cannot rate your own object.",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                ratingResult?.let { result ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = result,
                        color = if (result.contains("Success")) Color.Blue else Color.Red,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Message is hidden after 3 seconds
                    coroutineScope.launch {
                        delay(3000)
                        ratingResult = null
                    }
                }

            }
        } else {
            Text("Object not found", color = Color.Red, modifier = Modifier.fillMaxSize())
        }
        if (showRatingDialog) {
            AlertDialog(
                onDismissRequest = { showRatingDialog = false },
                title = { Text("Rate ${mapObject?.title}", fontSize = 24.sp) },
                text = {
                    Column {
                        Text("Rate from 1 to 10", fontSize = 18.sp,)
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text = "Current Rating: ${rating.toInt()}", style = MaterialTheme.typography.bodyLarge, fontSize = 24.sp)
                        Slider(
                            value = rating,
                            onValueChange = { rating = it },
                            valueRange = 1f..10f,
                            steps = 9,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                },
                dismissButton = {
                    Button(onClick = { showRatingDialog = false }) {
                        Text("Cancel", style = TextStyle(fontSize = 16.sp))
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        mapObject?.let {
                            objectViewModel.rateObject(it.id, rating.toInt()) { success, ownerId, difference ->
                                if (success) {
                                    ratingResult = "Rating Submitted Successfully"
                                    objectViewModel.getObjectById(objectId) { updatedObject ->
                                        if (updatedObject != null) {
                                            mapObject = updatedObject // Update the object with new average mark
                                        }
                                    }
                                    //If rating successfull, update the author's points
                                    ownerId?.let { id ->
                                        userViewModel.updateOwnerPoints(id, difference)
                                    }
                                } else {
                                    ratingResult = "Failed to Submit Rating"
                                }
                                showRatingDialog = false
                            }
                        }
                    }) {
                        Text("Submit", style = TextStyle(fontSize = 16.sp))
                    }
                }
            )
        }
    }
}




