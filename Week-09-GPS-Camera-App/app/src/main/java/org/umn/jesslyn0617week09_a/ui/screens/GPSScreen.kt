package org.umn.jesslyn0617week09_a.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.umn.jesslyn0617week09_a.data.GPSLocation
import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import java.util.Locale

@Composable
fun GPSScreen(){
    val context = LocalContext.current
    var location by remember {
        mutableStateOf<GPSLocation?>(null)
    }
    var states by remember { mutableStateOf("Get Location.")}
    var loading by remember { mutableStateOf(false)}
    fun requestLocation() {
        loading = true
        states = "Getting location..."
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                requestLocation()
            } else {
                states = "Location permission denied"
            }
        }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "GPS Location", style = MaterialTheme.typography.headlineSmall)
        Text(text = states)
        Button( onClick = {
            val fineGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (fineGranted || coarseGranted) {
                requestLocation()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) { Text ("Get Location")}
        if (loading) {
            CircularProgressIndicator()
        }
        location?.let { result ->
            Text(text = "Latitude: ${
                String.format(Locale.US,"%.6f", result.latitude)}")
            Text(text = "Longitude: ${
                String.format(Locale.US,"%.6f", result.longitude)}")
            Text(text = "Accuracy: ${
                String.format(Locale.US,"%.1f", result.accuracy)} meters")
        }
    }
}
fun getLocation(
    context: Context,
    onSuccess: (GPSLocation) -> Unit,
    onError: (String) -> Unit
) {
    val fineGranted = ContextCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    if (!fineGranted && !coarseGranted) {
        onError("Location permission has not been granted")
        return
    }
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    val priority = if (fineGranted) {
        Priority.PRIORITY_HIGH_ACCURACY
    } else {
        Priority.PRIORITY_BALANCED_POWER_ACCURACY
    }
    try {
        fusedClient.getCurrentLocation(
            priority,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if(location != null) {
                val result = GPSLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy
                )
                onSuccess(result)
            } else {
                onError("Location is unavailable. Please check your GPS settings.")
            }
        }.addOnFailureListener { error ->
            onError("Failed to retrieve location: ${error.message}")
        }
    } catch (e: SecurityException) {
        onError("Location access denied.")
    }
}