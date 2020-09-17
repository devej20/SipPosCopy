package net.sipconsult.jubensippos.ui.shoppingcart

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_cart.view.*
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.data.models.CartItem
import net.sipconsult.jubensippos.internal.glide.GlideApp

class ShoppingCartViewHolder(
    itemView: View,
    onSubClick: (Int) -> Unit,
    onAddClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var cartItemPosition: Int = 0

    fun bind(cartItem: CartItem, position: Int) {
        cartItemPosition = position
        itemView.textProductName.text = cartItem.product.description

        itemView.textQuantity.text = cartItem.getProductQuantityString()

        itemView.textProductSalesPrice.text = cartItem.product.displaySalesPrice()

        GlideApp.with(itemView.context).load(cartItem.product.imageUrl)
            .placeholder(R.drawable.juben_logo_landscape).into(itemView.imageProduct)
    }

    init {
        itemView.imageButtonDelete.setOnClickListener {
            onDeleteClick(cartItemPosition)
        }
        itemView.imageButtonSub.setOnClickListener {
            onSubClick(cartItemPosition)
        }
        itemView.imageButtonAdd.setOnClickListener {
            onAddClick(cartItemPosition)
        }
    }


}