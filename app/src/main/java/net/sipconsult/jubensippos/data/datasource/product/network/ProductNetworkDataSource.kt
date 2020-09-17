package net.sipconsult.jubensippos.data.datasource.product.network

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.network.response.Products

interface ProductNetworkDataSource {
    val downloadProducts: LiveData<Products>

    suspend fun fetchProducts()
}