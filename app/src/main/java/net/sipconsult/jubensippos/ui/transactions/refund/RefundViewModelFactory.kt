package net.sipconsult.jubensippos.ui.transactions.refund

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepository

class RefundViewModelFactory(
    private val transactionRepository: TransactionRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RefundViewModel(
            transactionRepository
        ) as T
    }
}