package net.sipconsult.jubensippos.ui.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepository

class ReceiptViewModelFactory(private val transactionRepository: TransactionRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReceiptViewModel(transactionRepository) as T
    }
}