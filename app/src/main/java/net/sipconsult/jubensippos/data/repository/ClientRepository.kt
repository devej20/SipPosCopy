package net.sipconsult.jubensippos.data.repository

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.ClientItem

interface ClientRepository {
    suspend fun getClients(): LiveData<List<ClientItem>>

    fun getClientsLocal(): LiveData<List<ClientItem>>
}