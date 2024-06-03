package com.teksiak.core.presentation.ui

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.teksiak.core.domain.run.Run
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Run.getLocationName(
    context: Context,
    callback: (String?) -> Unit
) {
    withContext(Dispatchers.IO) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Geocoder(context).getFromLocation(
                location.lat,
                location.long,
                1,
            ) { addresses ->
                val locationName = addresses.firstOrNull()?.run {
                    subLocality?.let {
                        return@run "${subLocality}, $locality"
                    }
                    "$locality, $countryName"
                }
                callback(locationName)
            }
        } else {
            val addresses = Geocoder(context).getFromLocation(
                location.lat,
                location.long,
                1,
            )
            val locationName = addresses?.firstOrNull()?.run {
                subLocality?.let {
                    return@run "${subLocality}, $locality"
                }
                "$locality, $countryName"
            }
            callback(locationName)
        }
    }
}