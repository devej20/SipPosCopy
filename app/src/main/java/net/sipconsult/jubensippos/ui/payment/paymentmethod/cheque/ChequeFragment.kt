package net.sipconsult.jubensippos.ui.payment.paymentmethod.cheque

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import net.sipconsult.jubensippos.R

class ChequeFragment : Fragment() {

    companion object {
        fun newInstance() = ChequeFragment()
    }

    private lateinit var viewModel: ChequeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cheque_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChequeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}