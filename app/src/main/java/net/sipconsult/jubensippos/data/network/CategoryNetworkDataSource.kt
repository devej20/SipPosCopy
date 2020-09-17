package net.sipconsult.jubensippos.data.network

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.network.response.ProductCategories

interface CategoryNetworkDataSource {

    val downloadCategories: LiveData<ProductCategories>

    suspend fun fetchCategories()
}