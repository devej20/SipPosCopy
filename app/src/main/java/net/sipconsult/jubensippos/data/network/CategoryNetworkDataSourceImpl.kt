package net.sipconsult.jubensippos.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sipconsult.jubensippos.data.network.response.ProductCategories
import net.sipconsult.jubensippos.internal.NoConnectivityException

class CategoryNetworkDataSourceImpl(
    private val sipShopApiService: SipShopApiService
) : CategoryNetworkDataSource {

    private val _downloadCategories = MutableLiveData<ProductCategories>()

    override val downloadCategories: LiveData<ProductCategories>
        get() = _downloadCategories

    override suspend fun fetchCategories() {
        try {
            val fetchedCategory = sipShopApiService.getProductCategoriesAsync().await()
            _downloadCategories.postValue(fetchedCategory)
        } catch (e: NoConnectivityException) {
            Log.d(TAG, "fetchCategories: No internet Connection ", e)
        }
    }

    companion object {
        private const val TAG: String = "CategoryNetworkDataSrc"
    }
}