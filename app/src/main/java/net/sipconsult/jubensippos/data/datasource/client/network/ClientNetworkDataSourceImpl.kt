package net.sipconsult.jubensippos.data.datasource.client.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.jubensippos.data.network.SipShopApiService
import net.sipconsult.jubensippos.data.network.response.Clients
import net.sipconsult.jubensippos.internal.NoConnectivityException

class ClientNetworkDataSourceImpl(private val sipShopApiService: SipShopApiService) :
    ClientNetworkDataSource {

    private val _downloadClients = MutableLiveData<Clients>()

    override val downloadClients: LiveData<Clients>
        get() = _downloadClients

    override suspend fun fetchClients() {
        try {
            val fetchedClients = sipShopApiService.getClientsAsync()
            _downloadClients.postValue(fetchedClients)
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "fetchProducts: No internet Connection ", e)
        }
    }

    companion object {
        private const val TAG: String = "ClientNetworkDataSrc"
    }
}