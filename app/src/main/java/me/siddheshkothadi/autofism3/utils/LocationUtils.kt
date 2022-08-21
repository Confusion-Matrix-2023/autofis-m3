package me.siddheshkothadi.autofism3.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitCurrentLocation(priority: Int): Location? {
    return suspendCancellableCoroutine {
        // to use for request cancellation upon coroutine cancellation
        val cts = CancellationTokenSource()
        getCurrentLocation(priority, cts.token)
            .addOnSuccessListener {location ->
                // remember location is nullable, this happens sometimes
                // when the request expires before an update is acquired
                it.resume(location) { throwable ->
                    Timber.e(throwable)
                }
            }.addOnFailureListener {e ->
                it.resumeWithException(e)
            }

        it.invokeOnCancellation {
            cts.cancel()
        }
    }
}

@SuppressLint("MissingPermission")
fun locationByGPSOrNetwork(context: Context) {
    var gpsLocation: Location? = null;
    var networkLocation: Location? = null;
    var longitude: String = ""
    var latitude: String = ""

    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val hasGps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val hasNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    val gpsLocationListener: LocationListener = LocationListener {
        gpsLocation = it
    }
    val networkLocationListener: LocationListener = LocationListener {
        networkLocation = it
    }

    if (hasGps) {
        Timber.i("has gps")
        lm.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            0F,
            gpsLocationListener
        )
    }
    if (hasNetwork) {
        Timber.i("has network")
        lm.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            5000,
            0F,
            networkLocationListener
        )
    }

    val lastKnownLocationByGps =
        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    lastKnownLocationByGps?.let {
        gpsLocation = it
    }
    val lastKnownLocationByNetwork =
        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    lastKnownLocationByNetwork?.let {
        networkLocation = it
    }
    if (gpsLocation != null && networkLocation != null) {
        if (gpsLocation!!.accuracy > networkLocation!!.accuracy) {
            latitude = gpsLocation!!.latitude.toString()
            latitude = gpsLocation!!.latitude.toString()
        } else {
            latitude = networkLocation!!.latitude.toString()
            latitude = networkLocation!!.latitude.toString()
        }
    }
    Timber.i("$latitude, $longitude")
}

suspend fun otherWayToGetLocation(context: Context) {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var longitude: String = ""
    var latitude: String = ""

    if(LocationManagerCompat.isLocationEnabled(lm)) {
        val priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val location = LocationServices
            .getFusedLocationProviderClient(context)
            .awaitCurrentLocation(priority)

        location?.let {
            longitude = it.longitude.toString()
            latitude = it.longitude.toString()
        }
        Timber.i("$latitude, $longitude")
    }
}