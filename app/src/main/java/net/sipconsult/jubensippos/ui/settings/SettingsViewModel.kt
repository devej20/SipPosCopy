package net.sipconsult.jubensippos.ui.settings

import androidx.lifecycle.ViewModel
import net.sipconsult.jubensippos.data.repository.location.LocationRepository
import net.sipconsult.jubensippos.internal.lazyDeferred

class SettingsViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    val locations by lazyDeferred {
        locationRepository.getLocations()
    }

//    val locations = liveData(Dispatchers.IO) {
//        val locations=  locationRepository.getLocations()
//
//        emit(locations)
//
//    }
}