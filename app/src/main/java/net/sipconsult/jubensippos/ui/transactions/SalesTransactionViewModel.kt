package net.sipconsult.jubensippos.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.data.models.LoggedInUser
import net.sipconsult.jubensippos.data.network.response.SalesTransactions
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepository
import net.sipconsult.jubensippos.data.repository.user.UserRepository
import net.sipconsult.jubensippos.internal.Result
import net.sipconsult.jubensippos.internal.lazyDeferred

class SalesTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {


    init {

//        if (userRepository.isLoggedIn()) {
        userRepository.loggedInUser.observeForever {
            _user.value = it
        }
//        }

    }

    private val _user = MutableLiveData<LoggedInUser>()
    val user: LiveData<LoggedInUser> = _user

    private val _transactionResult = MutableLiveData<SaleTransactionResult>()
    val transactionResult: LiveData<SaleTransactionResult> = _transactionResult

    val getSaleTransactions by lazyDeferred {
        transactionRepository.fetchSaleTransactions(user.value!!.id)
    }

    fun updateTransactionResult(result: Result<SalesTransactions>) {
        if (result is Result.Success) {
            _transactionResult.value =
                SaleTransactionResult(
                    success = result.data
                )
        } else {
            _transactionResult.value =
                SaleTransactionResult(error = R.string.voucher_failed)
        }
    }
}