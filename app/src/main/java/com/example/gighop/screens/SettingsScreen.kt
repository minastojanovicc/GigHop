package com.example.gighop.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gighop.MainActivity
import com.example.gighop.components.BottomNavigationBar
import com.example.gighop.service.LocationService
import com.example.gighop.viewmodel.AuthViewModel
import com.example.gighop.viewmodel.NotificationViewModel
import com.example.gighop.viewmodel.NotificationViewModelFactory
import com.example.gighop.viewmodel.UserViewModel
import com.example.gighop.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, authViewModel: AuthViewModel, userViewModel: UserViewModel) {
    val context = LocalContext.current
    var permissionsRequested by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(context)
    )
    val serviceRunningState by notificationViewModel.serviceRunningState.observeAsState(false)

    val distanceNotification by notificationViewModel.distanceNotification.observeAsState(100f)
    var sliderPosition by remember { mutableStateOf(distanceNotification) }

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Check permissions
    if (!permissionsRequested) {
        LaunchedEffect(Unit) {
            requestAllPermissions(context)
            permissionsRequested = true
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            notificationViewModel.setNotificationEnabled(false)
            LocationService.stopLocationService(context)
        }

        userViewModel.getCurrentUser()
    }

    val currentUser = userViewModel.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentUser?.username ?: "User",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )}
                    },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Logout", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            notificationViewModel.setNotificationEnabled(enabled = false)
                            notificationViewModel.setDistanceNotification(100f)
                            LocationService.stopLocationService(context)
                            NotificationManagerCompat.from(context).cancelAll();
                            authViewModel.logout(navController)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.logout),
                                contentDescription = "Logout",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentUser?.photoUrl?:R.drawable.homescreenimage)
                        .crossfade(true)  // nice fade-in animation
                        .build(),
                    contentDescription = currentUser?.username?:"username",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentUser?.fullname?:"Full name",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentUser?.phoneNumber?:"Phone number",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentUser?.email?:"E-mail",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
                Spacer(modifier = Modifier.height(56.dp))
                Text(
                    text = "Notification Service",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Button(
                    onClick = {
                        if (serviceRunningState) {
                            LocationService.stopLocationService(context)
                            notificationViewModel.setNotificationEnabled(enabled = false)
                        } else {
                            // Check permissions
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                LocationService.startLocationService(context)
                                notificationViewModel.setNotificationEnabled(enabled = true)
                            } else {
                                Toast.makeText(context, "Location permission is required to start the service.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (serviceRunningState) Color.Magenta else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = if (serviceRunningState) "Stop" else "Start", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Distance Slider
                Text(
                    text = "Notification within: ${sliderPosition.toInt()} meters",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(12.dp)
                )
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = {
                        // Save to DataStore when user releases slider
                        notificationViewModel.setDistanceNotification(sliderPosition)
                    },
                    valueRange = 50f..300f, // min 50m, max 300m
                    steps = 4,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    )
}

// Check and require all permissions
private fun requestAllPermissions(context: Context) {
    val permissionsNeeded = mutableListOf<String>()

    // Provera i dodavanje dozvole za notifikacije
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Provera i dodavanje dozvole za lokaciju
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Zahtevaj sve potrebne dozvole odjednom
    if (permissionsNeeded.isNotEmpty()) {
        ActivityCompat.requestPermissions(context as MainActivity, permissionsNeeded.toTypedArray(), REQUEST_CODE_PERMISSIONS)
    }
}

private const val REQUEST_CODE_PERMISSIONS = 1001



