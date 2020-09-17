package net.sipconsult.jubensippos.data.datasource.salesAgent.network

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.network.response.SalesAgents

interface SalesAgentNetworkDataSource {

    val downloadSalesAgents: LiveData<SalesAgents>

    suspend fun fetchSalesAgents()
}