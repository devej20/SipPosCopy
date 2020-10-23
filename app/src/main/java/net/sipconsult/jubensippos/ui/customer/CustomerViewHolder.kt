package net.sipconsult.jubensippos.ui.customer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.jubensippos.data.models.CustomerItem

class CustomerViewHolder(itemView: View, onCustomerClick: (CustomerItem) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var _customer: CustomerItem
    fun bind(customerItem: CustomerItem, position: Int) {
        _customer = customerItem
    }

    init {
        itemView.setOnClickListener { onCustomerClick(_customer) }
    }
}