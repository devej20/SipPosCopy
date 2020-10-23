package net.sipconsult.jubensippos.ui.payment.paymentmethod.loyalty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.loyalty_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.SharedViewModel
import net.sipconsult.jubensippos.data.models.Voucher
import net.sipconsult.jubensippos.databinding.LoyaltyFragmentBinding
import net.sipconsult.jubensippos.internal.Result
import net.sipconsult.jubensippos.ui.base.ScopedFragment

class LoyaltyFragment : ScopedFragment() {

    private var _binding: LoyaltyFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.loyalty_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        buildUI()
    }

    private fun buildUI() {

        groupLoyaltyLoading.visibility = View.GONE
        groupLoyaltyResult.visibility = View.GONE

        sharedViewModel.voucherResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                groupLoyaltyLoading.visibility = View.GONE

                result.error?.let {
                    groupLoyaltyScan.visibility = View.VISIBLE
                    showVoucherFailed(it)
                }
                result.success?.let {
                    groupLoyaltyResult.visibility = View.VISIBLE
                    updateUiWithVoucher(it)
                }
            })

        sharedViewModel.selectedPaymentMethod.observe(viewLifecycleOwner, Observer {

                paymentMethod ->
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
                    3 -> {
                        findNavController().navigate(R.id.cardFragment)
                    }
                }
            }
        })

        buttonScanQrCode.setOnClickListener {
            startScanZxing()
        }


    }

    private fun updateUiWithVoucher(voucher: Voucher) {
        val price = sharedViewModel.totalPrice.value
//        editTextLoyaltyDue.setText(price)
        editTextLoyaltyValue.setText(voucher.value)
    }

    private fun showVoucherFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun startScanZxing() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setPrompt("Scan QR Code")
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                scanResult(result)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun scanResult(result: IntentResult?) {
        Toast.makeText(activity, "Scanned: " + result!!.contents, Toast.LENGTH_SHORT).show()
        groupLoyaltyLoading.visibility = View.VISIBLE
        groupLoyaltyScan.visibility = View.GONE
        sharedViewModel.voucherCode = result.contents.toString()
        ldIn()
    }

    private fun ldIn() = launch {
        val result = sharedViewModel.getVoucher.await()
        if (result is Result.Success) {
            sharedViewModel._voucherResult.value =
                VoucherResult(
                    success = result.data
                )
        } else {
            sharedViewModel._voucherResult.value =
                VoucherResult(error = R.string.voucher_failed)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}