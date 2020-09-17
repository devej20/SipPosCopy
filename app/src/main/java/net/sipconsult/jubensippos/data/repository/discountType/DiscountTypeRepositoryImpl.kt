package net.sipconsult.jubensippos.data.repository.discountType

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sipconsult.jubensippos.data.datasource.discountTypes.local.DiscountTypesLocalDataSource
import net.sipconsult.jubensippos.data.datasource.discountTypes.network.DiscountTypesNetworkDataSource
import net.sipconsult.jubensippos.data.models.DiscountTypesItem

class DiscountTypeRepositoryImpl(
    private val networkDataSource: DiscountTypesNetworkDataSource,
    private val localDataSource: DiscountTypesLocalDataSource
) : DiscountTypeRepository {

    init {
        networkDataSource.downloadDiscountTypes.observeForever { currentProducts ->
            persistFetchedDiscountTypes(currentProducts)
        }
    }

    override suspend fun getDiscountTypes(): LiveData<List<DiscountTypesItem>> {
        return withContext(Dispatchers.IO) {
            initDiscountTypesData()
            return@withContext localDataSource.discountTypes
        }
    }

    private fun persistFetchedDiscountTypes(fetchedProducts: List<DiscountTypesItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            localDataSource.updateDiscountTypes(fetchedProducts)
        }
    }

    private suspend fun initDiscountTypesData() {
        fetchDiscountTypes()

    }

    private suspend fun fetchDiscountTypes() {
        networkDataSource.fetchDiscountTypes()
    }
}