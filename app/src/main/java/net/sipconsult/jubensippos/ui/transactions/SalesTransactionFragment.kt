package net.sipconsult.jubensippos.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.sales_transaction_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.data.models.SalesTransactionsItem
import net.sipconsult.jubensippos.ui.base.ScopedFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class SalesTransactionFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactory: SalesTransactionViewModelFactory by instance()
    private lateinit var viewModel: SalesTransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sales_transaction_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(SalesTransactionViewModel::class.java)
        bindUI()
    }

    private fun ldIn() = launch {
        val result = viewModel.getSaleTransactions.await()
        viewModel.updateTransactionResult(result)
    }

    private fun bindUI() = launch {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                ldIn()
            }
        })



        viewModel.transactionResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                groupSaleTransactionLoading.visibility = View.GONE

                result.error?.let {
//                    showVoucherFailed(it)
                }
                result.success?.let {
                    setupRecyclerView(it)
                }
            })


    }

    private fun setupRecyclerView(transactions: ArrayList<SalesTransactionsItem>) {
        val salesTransactionListAdapter =
            SalesTransactionListAdapter(::onTransactionClick)
        listSaleTransaction.adapter = salesTransactionListAdapter
        salesTransactionListAdapter.setSalesTransaction(transactions)
    }

    private fun onTransactionClick(pos: Int) {
    }

}