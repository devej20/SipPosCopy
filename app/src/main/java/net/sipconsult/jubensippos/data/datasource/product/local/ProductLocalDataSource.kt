package net.sipconsult.jubensippos.data.datasource.product.local

import androidx.lifecycle.LiveData
import net.sipconsult.jubensippos.data.models.ProductItem

interface ProductLocalDataSource {
    val products: LiveData<List<ProductItem>>

    fun updateProducts(products: List<ProductItem>)
}