package net.sipconsult.jubensippos.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.jubensippos.data.models.CartItem
import net.sipconsult.jubensippos.data.repository.salesAgent.SalesAgentRepository
import net.sipconsult.jubensippos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.jubensippos.internal.lazyDeferred

class HomeViewModel(
    private val salesAgentRepository: SalesAgentRepository
) : ViewModel() {

    val cartItems: LiveData<MutableList<CartItem>> = ShoppingCartRepository.cartItems

    val totalPrice: LiveData<Double> = ShoppingCartRepository.totalPrice


    fun removeCartItem(cartItemPos: Int) {
        ShoppingCartRepository.removeCartItem(cartItemPos)
    }

    fun increaseCartItemQuantity(itemPosition: Int) {
        ShoppingCartRepository.increaseCartItemQuantity(itemPosition)
    }

    fun decreaseCartItemQuantity(itemPosition: Int) {
        ShoppingCartRepository.decreaseCartItemQuantity(itemPosition)
    }

    fun removeALLCartItem() {
        ShoppingCartRepository.removeALLCartItem()
    }

    val salesAgents by lazyDeferred {
        salesAgentRepository.getSalesAgents()
    }


}