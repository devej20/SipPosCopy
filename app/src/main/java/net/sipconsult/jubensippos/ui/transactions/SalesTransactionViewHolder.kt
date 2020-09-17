package net.sipconsult.jubensippos.ui.transactions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_sales_transaction.view.*
import net.sipconsult.jubensippos.data.models.SalesTransactionsItem

class SalesTransactionViewHolder(
    itemView: View,
    onTransactionClick: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private var _position: Int = 0
    fun bind(
        salesTransaction: SalesTransactionsItem,
        position: Int
    ) {
        _position = position
        itemView.textSaleTransactionDate.text = salesTransaction.date
//        itemView.textSaleTransactionPaymentMethod.text = salesTransaction.paymentMethod.name
        itemView.textSaleTransactionReceiptNumber.text = salesTransaction.receiptNumber
        itemView.textSaleTransactionDescription.text = salesTransaction.description
        itemView.textSaleTransactionTotalSales.text = salesTransaction.totalSales.toString()
    }

    init {
        itemView.setOnClickListener {
            onTransactionClick(_position)
        }
    }
}