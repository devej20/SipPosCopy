package net.sipconsult.jubensippos.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

const val LOCATION = "location"

class LocationProviderImpl(context: Context) : LocationProvider {
    private val appContext = context.applicationContext

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun getLocation(): String {
        val selectedLocation = preferences.getString(LOCATION, "1")
        return selectedLocation!!

    }
}