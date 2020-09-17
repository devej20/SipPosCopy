package net.sipconsult.jubensippos.data.datasource.discountTypes.network

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.network.response.DiscountTypes

interface DiscountTypesNetworkDataSource {
    val downloadDiscountTypes: LiveData<DiscountTypes>

    suspend fun fetchDiscountTypes()
}