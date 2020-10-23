package net.sipconsult.jubensippos.data.repository.shoppingCart

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.paperdb.Paper
import net.sipconsult.jubensippos.data.models.CartItem
import java.text.DecimalFormat

object ShoppingCartRepository {

    private const val CART: String = "cart"

    val decimalFormater = DecimalFormat("0.00")

    private val _cartItems = MutableLiveData<MutableList<CartItem>>()

    val cartItems: LiveData<MutableList<CartItem>>
        get() = _cartItems

    private val _totalPrice = MutableLiveData<Double>()

    val totalPrice: LiveData<Double>
        get() = _totalPrice

    val totalCartPrice: Double
        get() = getCartPrice()

    private val _totalQuantity = MutableLiveData<Int>()

    val totalQuantity: LiveData<Int>
        get() = _totalQuantity


    init {
        _cartItems.value =
            getCart()
        _totalPrice.value =
            getCartPrice()
        _totalQuantity.value =
            getCartQuantity()
    }

    fun addCartItem(cartItem: CartItem) {
        val cart =
            getCart()

        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem == null) {
            cart.add(cartItem)
        } else {
            targetItem.quantity++
        }

        saveCart(
            cart
        )
        _cartItems.value =
            getCart()
        _totalPrice.value =
            getCartPrice()
//        AddCartItemAsyncTask().execute(cartItem)
    }

    fun removeCartItem(cartItem: CartItem) {
        val cart =
            getCart()
        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
//        val cartItem = cart[cartItem]

        cart.remove(targetItem)

        saveCart(
            cart
        )

        _cartItems.value =
            getCart()
        _totalPrice.value =
            getCartPrice()
//        RemoveCartItemAsyncTask().execute(item)
    }


    fun increaseCartItemQuantity(cartItem: CartItem) {
        val cart =
            getCart()

        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem != null) {
            targetItem.quantity++
        }
        saveCart(
            cart
        )

        _cartItems.value =
            getCart()
        _totalPrice.value =
            getCartPrice()

//        IncreaseCartItemQuantityAsyncTask().execute(item)
    }

    fun decreaseCartItemQuantity(cartItem: CartItem) {
        val cart =
            getCart()
        val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
        if (targetItem != null) {
            if (targetItem.quantity > 1) {
                targetItem.quantity--
            }
        }

        saveCart(
            cart
        )

        _cartItems.value =
            getCart()
        _totalPrice.value =
            getCartPrice()

//        DecreaseCartItemQuantityAsyncTask().execute(item)
    }

    fun removeALLCartItem() {
        deleteCart()
        _cartItems.value =
            getCart()
        _totalPrice.value =
            getCartPrice()
    }

    private fun saveCart(cart: MutableList<CartItem>) {
        Paper.book().write(CART, cart)
    }

    fun getCart(): MutableList<CartItem> {
        return Paper.book().read(CART, mutableListOf())
    }

    fun deleteCart() = Paper.book().delete(CART)

    fun getShoppingCartSize(): Int {
        var cartSize = 0
        getCart()
            .forEach {
                cartSize += it.quantity
            }

        return cartSize
    }

    private fun getCartPrice(): Double {
        var price = 0.0
        _cartItems.value?.forEach {
            price += (it.product.price.salePrice * it.quantity)
        }
        return price
    }

    private fun getCartQuantity(): Int {
        var quantity = 0
        _cartItems.value?.forEach {
            quantity += it.quantity
        }
        return quantity
    }


    //    companion object {
    class AddCartItemAsyncTask :
        AsyncTask<CartItem, Void, Void>() {
        override fun doInBackground(vararg params: CartItem): Void? {
            val cart =
                getCart()

            val targetItem = cart.singleOrNull { it.product.code == params[0].product.code }
            if (targetItem == null) {
                cart.add(params[0])
            }

            saveCart(
                cart
            )
            return null
        }

    }

    class RemoveCartItemAsyncTask : AsyncTask<Int, Void, Void>() {
        override fun doInBackground(vararg params: Int?): Void? {
            val cart =
                getCart()
            val cartItem = cart[params[0]!!]
            cart.remove(cartItem)
            saveCart(
                cart
            )
            return null
        }
    }

    class IncreaseCartItemQuantityAsyncTask :
        AsyncTask<Int, Void, Void>() {
        override fun doInBackground(vararg params: Int?): Void? {
            val cart =
                getCart()
            val cartItem = cart[params[0]!!]

            val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
            if (targetItem != null) {
                targetItem.quantity++
                Log.d(
                    "ShoppingCartRepository",
                    "increaseCartItemQuantity: ${targetItem.quantity}"
                )
            }
            saveCart(
                cart
            )
            return null
        }
    }

    class DecreaseCartItemQuantityAsyncTask :
        AsyncTask<Int, Void, Void>() {
        override fun doInBackground(vararg params: Int?): Void? {
            val cart =
                getCart()
            val cartItem = cart[params[0]!!]
            val targetItem = cart.singleOrNull { it.product.code == cartItem.product.code }
            if (targetItem != null) {
                if (targetItem.quantity > 1) {
                    targetItem.quantity--
                    Log.d(
                        "ShoppingCartRepository",
                        "decreaseCartItemQuantity: ${targetItem.quantity}"
                    )
                }
            }
//
            saveCart(
                cart
            )
            return null
        }
    }

    class RemoveALLCartItemAsyncTask :
        AsyncTask<Int, Void, Void>() {
        override fun doInBackground(vararg params: Int?): Void? {
            deleteCart()
            return null
        }
    }


//    }


}