package net.sipconsult.jubensippos.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sipconsult.jubensippos.data.provider.LocationProvider
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepository

class SalesTransactionViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val locationProvider: LocationProvider
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SalesTransactionViewModel(
            transactionRepository,
            locationProvider
        ) as T
    }
}