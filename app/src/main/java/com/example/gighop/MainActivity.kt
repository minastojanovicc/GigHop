package com.example.gighop

import MainApp
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gighop.ui.theme.GigHopTheme

class MainActivity : ComponentActivity() {

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var hasLocationPermission by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val denied = permissions.filterValues { !it }.keys

                if (denied.isEmpty()) {
                    Log.d("Permissions", "All permissions granted.")
                 } else {
                    Log.d("Permissions", "Denied: $denied")
                    Toast.makeText(
                        this,
                        "Permissions are required for the app to function correctly.",
                        Toast.LENGTH_SHORT
                    ).show()

                    denied.forEach { perm ->
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                            Log.d("Permissions", "User denied $perm, should show rationale.")
                        } else {
                            Toast.makeText(
                                this,
                                "Permission denied permanently. Enable it in settings.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

        checkPermissions()

        setContent {
            GigHopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainApp(hasLocationPermission = hasLocationPermission)
                }
            }
        }
    }

    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )

        val notGranted = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            permissionsLauncher.launch(notGranted.toTypedArray())
        } else {
            hasLocationPermission = true
            Log.d("Permissions", "All permissions already granted.")
        }
    }
}

