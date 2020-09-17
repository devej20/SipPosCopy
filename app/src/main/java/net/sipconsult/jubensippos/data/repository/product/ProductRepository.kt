package net.sipconsult.jubensippos.data.repository.product

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.ProductItem

interface ProductRepository {
    suspend fun getProducts(): LiveData<List<ProductItem>>
    fun getProductsLocal(): LiveData<List<ProductItem>>

}