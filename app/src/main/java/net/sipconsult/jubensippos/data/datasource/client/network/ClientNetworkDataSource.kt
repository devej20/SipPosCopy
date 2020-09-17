package net.sipconsult.jubensippos.data.datasource.client.network

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.network.response.Clients

interface ClientNetworkDataSource {
    val downloadClients: LiveData<Clients>

    suspend fun fetchClients()
}