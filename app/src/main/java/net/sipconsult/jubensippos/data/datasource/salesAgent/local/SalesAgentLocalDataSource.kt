package net.sipconsult.jubensippos.data.datasource.salesAgent.local

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.SalesAgentsItem

interface SalesAgentLocalDataSource {
    val salesAgents: LiveData<List<SalesAgentsItem>>

    fun salesAgent(salesAgentId: Int): SalesAgentsItem

    fun updateSalesAgent(salesAgents: List<SalesAgentsItem>)
}