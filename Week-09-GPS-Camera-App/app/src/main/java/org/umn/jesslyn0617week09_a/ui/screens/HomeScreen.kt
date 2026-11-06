package org.umn.jesslyn0617week09_a.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onOpenGps: () -> Unit,
    onOpenCamera: () -> Unit
){
    Column(modifier = Modifier.fillMaxSize()
        .verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Text("GPS and Camera Application Using Jetpack Compose.")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Text("GPS Location", style = MaterialTheme.typography.titleMedium)
                Text("Show the latitude and longitude.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onOpenGps, modifier = Modifier.fillMaxWidth()){
                    Text("Open GPS")
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Text("Camera", style = MaterialTheme.typography.titleMedium)
                Text("Take a picture and Preview.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onOpenCamera,
                    modifier = Modifier.fillMaxWidth()
                ){ Text("Open your Camera") }
            }
        }
    }
}
