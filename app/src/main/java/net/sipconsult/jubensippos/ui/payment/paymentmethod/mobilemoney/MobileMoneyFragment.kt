package net.sipconsult.jubensippos.ui.payment.paymentmethod.mobilemoney

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.SharedViewModel
import net.sipconsult.jubensippos.databinding.MobileMoneyFragmentBinding
import net.sipconsult.jubensippos.ui.base.ScopedFragment

class MobileMoneyFragment : ScopedFragment() {

    private var _binding: MobileMoneyFragmentBinding? = null

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

        _binding = MobileMoneyFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel.editTextMobileMoneyPhoneNumber.observe(
            viewLifecycleOwner,
            Observer { phoneNumber ->

                if (!phoneNumber.isNullOrEmpty()) {
                    sharedViewModel.mobileMoneyPhoneNumber = phoneNumber.trim()
                } else {
                    sharedViewModel.mobileMoneyPhoneNumber = ""
                }

            })

        sharedViewModel.editTextMobileMoneyAmount.observe(viewLifecycleOwner, Observer { amount ->
            if (!amount.isNullOrEmpty()) {
                sharedViewModel.mobileMoneyAmount = amount.trim().toDouble()
                sharedViewModel.deduct()
            } else {
                sharedViewModel.mobileMoneyAmount = 0.0
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
//                        2 -> {
//                            findNavController().navigate(R.id.mobileMoneyFragment)
//                        }
                        4 -> {
                            findNavController().navigate(R.id.loyaltyFragment)
                        }
                        3 -> {
                            findNavController().navigate(R.id.visaFragment)
                        }
                    }
                }
            })

//        editTextMobileMoneyPhoneNumber.setRawInputType(InputType.TYPE_CLASS_TEXT)
//        editTextMobileMoneyPhoneNumber.setTextIsSelectable(true)
//        editTextMobileMoneyPhoneNumber.setOnClickListener { hideKeyboard() }

//        editTextMobileMoneyAmount.setRawInputType(InputType.TYPE_CLASS_TEXT)
//        editTextMobileMoneyAmount.setTextIsSelectable(true)
//        editTextMobileMoneyAmount.setOnClickListener { hideKeyboard() }

//        val icPhoneNumber: InputConnection =
//            editTextMobileMoneyPhoneNumber.onCreateInputConnection(EditorInfo())!!

//        val icAmount: InputConnection =
//            editTextMobileMoneyAmount.onCreateInputConnection(EditorInfo())!!

//        keyboardPayment.setInputConnection(icPhoneNumber)
//        keyboardPayment.setInputConnection(icAmount)
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