package net.sipconsult.jubensippos.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.jubensippos.data.models.CartItem
import net.sipconsult.jubensippos.data.models.ProductItem
import net.sipconsult.jubensippos.data.repository.location.LocationRepository
import net.sipconsult.jubensippos.data.repository.product.ProductRepository
import net.sipconsult.jubensippos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.jubensippos.internal.lazyDeferred

class ProductsViewModel(
    private val productRepository: ProductRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    var productItems: List<ProductItem> = arrayListOf()
    var localproducts: LiveData<List<ProductItem>> = productRepository.getProductsLocal()

    fun addCartItem(product: ProductItem) {
        val cartItem = CartItem(product)
        cartItem.let { ShoppingCartRepository.addCartItem(it) }
    }

    fun addScannedCartItem(barcode: String) {
        val pdt = productItems.find { p -> p.barcode == barcode }
        val cartItem = pdt?.let { CartItem(it) }
        cartItem.let { it?.let { it1 -> ShoppingCartRepository.addCartItem(it1) } }
    }

    val products by lazyDeferred {
        productRepository.getProducts()
    }

    val locations by lazyDeferred {
        locationRepository.getLocations()
    }

}
