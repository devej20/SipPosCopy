package net.sipconsult.jubensippos.data.datasource.salesAgent.local

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.db.SalesAgentDao
import net.sipconsult.jubensippos.data.models.SalesAgentsItem

class SalesAgentLocalDataSourceImpl(
    private val salesAgentDao: SalesAgentDao
) : SalesAgentLocalDataSource {

    override val salesAgents: LiveData<List<SalesAgentsItem>>
        get() = salesAgentDao.getSalesAgents

    override fun salesAgent(salesAgentId: Int): SalesAgentsItem {
        return salesAgentDao.getSalesAgent(salesAgentId)
    }

    override fun updateSalesAgent(salesAgents: List<SalesAgentsItem>) {
        salesAgentDao.deleteAll()
        salesAgentDao.upsertAll(salesAgents)
    }
}