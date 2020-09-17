package net.sipconsult.jubensippos.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepository
import net.sipconsult.jubensippos.data.repository.user.UserRepository

class SalesTransactionViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SalesTransactionViewModel(
            transactionRepository,
            userRepository
        ) as T
    }
}