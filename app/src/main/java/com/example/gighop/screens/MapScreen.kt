package com.example.gighop.screens

import Screen
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gighop.components.BottomNavigationBar
import com.example.gighop.viewmodel.ObjectState
import com.example.gighop.viewmodel.ObjectViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    objectViewModel: ObjectViewModel,
    hasLocationPermission: Boolean,
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var locationErrorMessage by remember { mutableStateOf<String?>(null) }

    var showAddObjectDialog by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }

    val nis = LatLng(43.321445, 21.896104)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nis, 17f)
    }

    // Access to Firebase and collect objects
    LaunchedEffect(Unit) {
        objectViewModel.fetchAllObjects()
    }

    // Continuously track user location if permission is granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            startContinuousLocationUpdates(context, fusedLocationClient) { location ->
                currentLocation = location
            }
        }
    }


    if(currentLocation != null) {
        LaunchedEffect(key1 = true) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                   CameraPosition(LatLng(currentLocation!!.latitude, currentLocation!!.longitude), 17f, 0f, 0f)
                ),
                durationMs = 1000
            )
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GigHop") },
                navigationIcon = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = if (showFilters) "Hide Filters" else "Show Filters"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddObjectDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Object"
                        )
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
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Map
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = hasLocationPermission
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true,
                            tiltGesturesEnabled = true,
                            myLocationButtonEnabled = true
                        )
                    ) {
                        val objects =
                            (objectViewModel.objectState as? ObjectState.ObjectsFetched)?.objects ?: emptyList()
                        objects.forEach { mapObject ->
                            Marker(
                                state = MarkerState(
                                    LatLng(mapObject.latitude, mapObject.longitude)
                                ),
                                title = mapObject.title,
                                snippet = mapObject.subject,
                                onInfoWindowClick={
                                    navController.navigate(Screen.Object.name + "/${mapObject.id}")
                                }
                            )
                        }
                    }

                    // Loading indicator
                    if (objectViewModel.objectState is ObjectState.Loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // Error Snackbar
                    if (objectViewModel.objectState is ObjectState.Error) {
                        val state = objectViewModel.objectState as ObjectState.Error
                        Snackbar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) { Text(text = state.message) }
                    }
                }
            }
            
            // Filter dialog
            if (showFilters) {
                FilterObjectDialog(
                    objectViewModel = objectViewModel,
                    onApplyFilters = { author, type, subject, rating, startDate, endDate, radius ->
                        currentLocation?.let { loc ->
                            objectViewModel.applyFilters(
                                author, type, subject, rating,
                                startDate, endDate, radius,
                                LatLng(loc.latitude, loc.longitude)
                            )
                        }
                        showFilters = false
                    },
                    onClearFilters = {
                        objectViewModel.clearFilters()
//                        showFilters = false
                    },
                    onDismiss = {
                        objectViewModel.clearFilters()
                        showFilters = false
                    }
                )
            }

            // Add Object Dialog
            if (showAddObjectDialog) {
                AddObjectDialog(
                    onDismiss = { showAddObjectDialog = false },
                    onSave = { title, subject, description, selectedImageUri, type ->
                        currentLocation?.let { loc ->
                            objectViewModel.addObject(
                                title, subject, description,
                                loc.latitude, loc.longitude, selectedImageUri, type
                            )
                            showAddObjectDialog = false
                        }
                    }
                )
            }

            // Location error Snackbar
            locationErrorMessage?.let { message ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) { Text(message) }
            }
        }
    }
}


@SuppressLint("MissingPermission")
fun startContinuousLocationUpdates(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
        .setMinUpdateIntervalMillis(2000L)
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            onLocationReceived(locationResult.lastLocation)
        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
}


