package net.sipconsult.jubensippos.ui.payment.paymentmethod.cash

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.cash_fragment.*
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.SharedViewModel
import net.sipconsult.jubensippos.databinding.CashFragmentBinding
import net.sipconsult.jubensippos.ui.base.ScopedFragment


class CashFragment : ScopedFragment() {

    private var _binding: CashFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        _binding = CashFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val view = binding.root

        sharedViewModel.editTextTender.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                sharedViewModel.cashAmount = it.trim().toDouble()
                sharedViewModel.deduct()
            } else {
                sharedViewModel.cashAmount = 0.0
                sharedViewModel.deduct()
            }
        })

        sharedViewModel.selectedPaymentMethod.observe(
            viewLifecycleOwner,
            Observer { paymentMethod ->


                if (paymentMethod != null) {

                    when (paymentMethod.id) {
//                        1 -> {
//                            findNavController().navigate(R.id.cashFragment)
//                        }
                        2 -> {
                            findNavController().navigate(R.id.mobileMoneyFragment)
                        }
                        4 -> {
                            findNavController().navigate(R.id.loyaltyFragment)
                        }
                        3 -> {
                            findNavController().navigate(R.id.visaFragment)
                        }
                    }
                }

            })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        buildUI()
    }

    private fun buildUI() {
        editTextCashTender.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editTextCashTender.setTextIsSelectable(true)
        editTextCashTender.setOnClickListener { hideKeyboard() }
//        editTextCashTender.keyListener = null

        val ic: InputConnection = editTextCashTender.onCreateInputConnection(EditorInfo())!!
        keyboardPayment.setInputConnection(ic)

        disableEditText(editTextCashDue)
        disableEditText(editTextCashChange)
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
//        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
//        editText.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun Fragment.hideKeyboard() {
        view?.let {
            activity?.hideKeyboard(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}