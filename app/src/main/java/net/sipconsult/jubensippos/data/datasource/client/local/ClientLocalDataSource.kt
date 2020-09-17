package net.sipconsult.jubensippos.data.datasource.client.local

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.ClientItem

interface ClientLocalDataSource {
    val clients: LiveData<List<ClientItem>>

    fun updateClients(clients: List<ClientItem>)
}