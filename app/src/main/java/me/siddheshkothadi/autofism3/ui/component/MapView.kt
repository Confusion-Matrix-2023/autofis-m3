package me.siddheshkothadi.autofism3.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapView(
    latitude: String, longitude: String, modifier: Modifier = Modifier
) {
    if (latitude.isBlank() or longitude.isBlank()) {
        CircularProgressIndicator()
    } else {
        val location = LatLng(latitude.toDouble(), longitude.toDouble())
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, 10f)
        }
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
        ) {
            Marker(position = location)
        }
    }
}