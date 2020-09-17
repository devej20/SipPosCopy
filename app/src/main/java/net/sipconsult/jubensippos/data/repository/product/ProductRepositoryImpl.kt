package net.sipconsult.jubensippos.data.repository.product

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sipconsult.jubensippos.data.datasource.product.local.ProductLocalDataSource
import net.sipconsult.jubensippos.data.datasource.product.network.ProductNetworkDataSource
import net.sipconsult.jubensippos.data.models.ProductItem

class ProductRepositoryImpl(
    private val networkDataSource: ProductNetworkDataSource,
    private val localDataSource: ProductLocalDataSource
) : ProductRepository {

    init {
        networkDataSource.downloadProducts.observeForever { currentProducts ->
            persistFetchedProducts(currentProducts)
        }
    }

    override suspend fun getProducts(): LiveData<List<ProductItem>> {
        return withContext(Dispatchers.IO) {
            initProductsData()
            return@withContext localDataSource.products
        }
    }

    override fun getProductsLocal(): LiveData<List<ProductItem>> {
        return localDataSource.products
    }

    private fun persistFetchedProducts(fetchedProducts: List<ProductItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            localDataSource.updateProducts(fetchedProducts)
        }
    }

    private suspend fun initProductsData() {
        fetchProducts()

    }

    private suspend fun fetchProducts() {
        networkDataSource.fetchProducts()
    }
}