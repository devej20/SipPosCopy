package net.sipconsult.jubensippos.data.datasource.location.network

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.network.response.Locations

interface LocationNetworkDataSource {
    val downloadLocations: LiveData<Locations>

    suspend fun fetchLocations()
}