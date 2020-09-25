package net.sipconsult.jubensippos.ui.payment.paymentmethod.visa

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.visa_fragment.*
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.SharedViewModel
import net.sipconsult.jubensippos.databinding.VisaFragmentBinding
import net.sipconsult.jubensippos.ui.base.ScopedFragment

class VisaFragment : ScopedFragment() {

    private var _binding: VisaFragmentBinding? = null

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

        _binding = VisaFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel.editTextVisaCardNumber.observe(viewLifecycleOwner, Observer { cardNumber ->

            if (!cardNumber.isNullOrEmpty()) {
                sharedViewModel.visaCardNumber = cardNumber.trim()
            } else {
                sharedViewModel.visaCardNumber = ""
            }

        })

        sharedViewModel.editTextVisaAmount.observe(viewLifecycleOwner, Observer { amount ->
            if (!amount.isNullOrEmpty()) {
                sharedViewModel.visaAmount = amount.trim().toDouble()
                sharedViewModel.deduct()
            } else {
                sharedViewModel.visaAmount = 0.0
                sharedViewModel.deduct()
            }

        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        buildUI()
    }

    private fun buildUI() {
        sharedViewModel.selectedPaymentMethod.observe(
            viewLifecycleOwner,
            Observer { paymentMethod ->
                if (paymentMethod != null) {

                    when (paymentMethod.id) {
                        1 -> {
                            findNavController().navigate(R.id.cashFragment)
                        }
                        2 -> {
                            findNavController().navigate(R.id.mobileMoneyFragment)
                        }
                        4 -> {
                            findNavController().navigate(R.id.loyaltyFragment)
                        }
//                        3 -> {
//                            findNavController().navigate(R.id.visaFragment)
//                        }
                    }
                }
            })

        keyboardVisa.visibility = View.GONE
        editTextVisaCardNumber.visibility = View.GONE
        textVisa.visibility = View.GONE

//        editTextVisaCardNumber.setRawInputType(InputType.TYPE_CLASS_TEXT)
//        editTextVisaCardNumber.setTextIsSelectable(true)
//        editTextVisaCardNumber.setOnClickListener { hideKeyboard() }
//
//        editTextVisaAmount.setRawInputType(InputType.TYPE_CLASS_TEXT)
//        editTextVisaAmount.setTextIsSelectable(true)
//        editTextVisaAmount.setOnClickListener { hideKeyboard() }
//
//        val icCardNumber: InputConnection =
//            editTextVisaCardNumber.onCreateInputConnection(EditorInfo())!!
//
//        val icAmount: InputConnection =
//            editTextVisaAmount.onCreateInputConnection(EditorInfo())!!
//
//        keyboardPayment.setInputConnection(icCardNumber)
//        keyboardPayment.setInputConnection(icAmount)

        disableEditText(editTextVisaDue)
        disableEditText(editTextVisaChange)
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