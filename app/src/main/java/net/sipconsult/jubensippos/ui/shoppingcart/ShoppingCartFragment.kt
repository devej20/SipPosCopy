package net.sipconsult.jubensippos.ui.shoppingcart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.shopping_cart_fragment.*
import net.sipconsult.jubensippos.R

class ShoppingCartFragment : Fragment() {

    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ShoppingCartViewModel::class.java)
        val root = inflater.inflate(R.layout.shopping_cart_fragment, container, false)

        shoppingCartAdapter =
            ShoppingCartAdapter(
                ::onSubClick,
                ::onAddClick,
                ::onDeleteClick
            )
        listShoppingCart.layoutManager = LinearLayoutManager(context)
        listShoppingCart.adapter = shoppingCartAdapter
        viewModel.cartItems.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                listShoppingCart.visibility = View.INVISIBLE
                groupCart.visibility = View.VISIBLE
            } else {
                listShoppingCart.visibility = View.VISIBLE
                groupCart.visibility = View.INVISIBLE
            }
            shoppingCartAdapter.setCartItems(it)
        })

        return root
    }

    private fun onDeleteClick(itemPosition: Int) {
        viewModel.removeCartItem(itemPosition)
    }

    private fun onSubClick(itemPosition: Int) {
        viewModel.decreaseCartItemQuantity(itemPosition)
    }

    private fun onAddClick(itemPosition: Int) {
        viewModel.increaseCartItemQuantity(itemPosition)
    }

}