package net.sipconsult.jubensippos.ui.payment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.dialog_authentication.view.*
import kotlinx.android.synthetic.main.payment_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.jubensippos.MainActivity
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.SharedViewModel
import net.sipconsult.jubensippos.data.models.DiscountTypesItem
import net.sipconsult.jubensippos.data.models.LoggedInUser
import net.sipconsult.jubensippos.data.models.PaymentMethodItem
import net.sipconsult.jubensippos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.jubensippos.databinding.PaymentFragmentBinding
import net.sipconsult.jubensippos.ui.base.ScopedFragment
import net.sipconsult.jubensippos.ui.discountType.DiscountListAdapter
import net.sipconsult.jubensippos.ui.login.AuthenticationState
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import woyou.aidlservice.jiuiv5.IWoyouService
import java.text.DecimalFormat
import java.util.*


class PaymentFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private var _binding: PaymentFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var paymentMethodListAdapter: PaymentMethodListAdapter

    private lateinit var viewModel: PaymentViewModel
    private val viewModelFactory: PaymentViewModelFactory by instance()

    private var selectedDiscountType: DiscountTypesItem? = null

    private var woyouService: IWoyouService? = null
    private val connService: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            woyouService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            woyouService = IWoyouService.Stub.asInterface(service)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedViewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(PaymentViewModel::class.java)

        val intent = Intent()
        intent.setPackage("woyou.aidlservice.jiuiv5")
        intent.action = "woyou.aidlservice.jiuiv5.IWoyouService"
        context?.bindService(intent, connService, Context.BIND_AUTO_CREATE)

        val navController = NavHostFragment.findNavController(this)

        sharedViewModel.authenticationState.observe(
            viewLifecycleOwner,
            Observer { authenticationState ->
                when (authenticationState) {
//                AuthenticationState.AUTHENTICATED -> showWelcomeMessage()
                    AuthenticationState.UNAUTHENTICATED -> navController.navigate(R.id.loginFragment)
                }
            })

        _binding = PaymentFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bindUI()
    }

    private fun navigateToReceipt() {
        sharedViewModel.buildReceipt()
        displayName("Juben")
        val action =
            PaymentFragmentDirections.actionPaymentFragmentToReceiptFragment()
        findNavController().navigate(action)
        sharedViewModel.resetTransaction()

    }

    private fun bindUI() = launch {

        groupPaymentMethodTransaction.visibility = View.GONE

        sharedViewModel.transactionResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                groupPaymentMethodTransaction.visibility = View.GONE

                result.getContentIfNotHandled()?.let { transactionResult ->

                    transactionResult.error?.let {
                        showTransactionFailed(it)
                    }
                    transactionResult.success?.let {
                        sharedViewModel.resetTransaction()
                        result.hasBeenHandled()
                        navigateToReceipt()
                    }
                }


            })

        val paymentMethods = sharedViewModel.getPaymentMethods.await()
        paymentMethods.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            groupLoading.visibility = View.GONE
            setupRecyclerViewPaymentMethod(it as ArrayList<PaymentMethodItem>)

        })

        val discountTypes = sharedViewModel.discountTypes.await()
        discountTypes.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            groupLoadingDiscount.visibility = View.GONE
            setupRecyclerViewDiscountType(it as ArrayList<DiscountTypesItem>)
        })

        sharedViewModel.totalPrice.observe(viewLifecycleOwner, Observer { total ->
            textTotalAmountCart.text = String.format("GHC%s", DecimalFormat("0.00").format(total))

            sharedViewModel.change.observe(viewLifecycleOwner, Observer { change ->
                displayTotalPriceChange(
                    String.format(
                        "Total:GHC%s",
                        DecimalFormat("0.00").format(total)
                    ), String.format(
                        "Change:%s",
                        change
                    )
                )

            })


        })

        sharedViewModel.paymentMethods.observe(viewLifecycleOwner, Observer {

            if (it.isNullOrEmpty()) {
                groupPaymentMethod.visibility = View.VISIBLE
                framePaymentMethod.visibility = View.GONE
//                Toast.makeText(context, "Please selected Payment Method", Toast.LENGTH_SHORT).show()
            } else {
                groupPaymentMethod.visibility = View.GONE
                framePaymentMethod.visibility = View.VISIBLE
            }
        })

        sharedViewModel.salesAgent.observe(viewLifecycleOwner, Observer { saleAgent ->
            if (saleAgent != null) {
                textSalesAgentName.text = saleAgent.displayName

            } else {
                textSalesAgentName.text = "Sale Agent not Selected"
            }

        })

        sharedViewModel.editTextDeliveryCost.observe(viewLifecycleOwner, Observer { deliveryCost ->
            if (!deliveryCost.isNullOrEmpty()) {
                sharedViewModel.deliveryCost = deliveryCost.trim().toDouble()
                sharedViewModel.addDeliveryCost()
            } else {
                sharedViewModel.deliveryCost = 0.0
            }

        })

        sharedViewModel.discountType.observe(viewLifecycleOwner, Observer {
            selectedDiscountType = it
            val totalPrice: Double = ShoppingCartRepository.totalPrice.value!!
            if (selectedDiscountType != null) {
                val percentage = selectedDiscountType?.percentage!!

                val salePriceDiscount = percentage.toDouble() / 100
                val discountPrice: Double = salePriceDiscount.times(totalPrice)
                val newTotalPrice: Double = totalPrice - discountPrice

                textPaymentDiscount.visibility = View.VISIBLE

                textPaymentDiscount.text =
                    String.format(
                        "with a %s %% Discount, GHC %s",
                        percentage,
                        DecimalFormat("0.00").format(discountPrice)
                    )

                sharedViewModel.updateTotalPrice(newTotalPrice)
                sharedViewModel.updateDiscountPrice(discountPrice)


            } else {

                sharedViewModel.updateTotalPrice(totalPrice)
                textPaymentDiscount.visibility = View.INVISIBLE
            }
        })

        buttonPaymentBack.setOnClickListener {
            sharedViewModel.resetPaymentMethodAndDiscount()
            activity?.finish()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
//            findNavController().popBackStack()
        }

        buttonPaymentValidate.setOnClickListener {

            val totalPrice: Double = sharedViewModel.totalPrice.value!!
//            val paymentMethods = paymentMethodListAdapter.selectedPaymentMethods

//            sharedViewModel.updatePaymentMethods(paymentMethods)

            sharedViewModel.paymentMethods.observe(viewLifecycleOwner, Observer { paymentMethods ->

                if (paymentMethods.isNullOrEmpty()) {

                    Toast.makeText(context, "Please Selected Payment Method", Toast.LENGTH_SHORT)
                        .show()

                } else {

                    for (p in paymentMethods) {
                        if (p.id == 2) {

                            if (sharedViewModel.mobileMoneyAmount < 0) {
                                Toast.makeText(
                                    context,
                                    "Please Enter Mobile Money Amount",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Observer
                            }
                        }

                    }

                    for (p in paymentMethods) {
                        if (p.id == 3) {

                            if (sharedViewModel.visaAmount < 0) {
                                Toast.makeText(
                                    context,
                                    "Please Enter Payment Card Amount",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Observer
                            }
                        }

                    }
                    val totalAmount =
                        sharedViewModel.cashAmount + sharedViewModel.mobileMoneyAmount + sharedViewModel.visaAmount

                    if (totalAmount < totalPrice) {
                        Toast.makeText(context, "Invalid Total Amount", Toast.LENGTH_SHORT)
                            .show()
                        return@Observer
                    }

                    groupPaymentMethodTransaction.visibility = View.VISIBLE
                    sharedViewModel.generateReceiptNumber()
//                    navigateToReceipt()
                    post()

                }

            })
        }
    }

    fun displayName(name: String) {
        if (woyouService == null) {
//            Toast.makeText(context, "Service not ready", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            woyouService!!.sendLCDString(name, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun displayTotalPriceChange(totalPrice: String, change: String) {
        if (woyouService == null) {
//            Toast.makeText(context, "Service not ready", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            //woyouService.sendLCDCommand(2);
            woyouService!!.sendLCDDoubleString(totalPrice, change, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun post() = launch {
        val result = sharedViewModel.postTransaction.await()
        sharedViewModel.updateTransactionResult(result)

    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Fragment.hideKeyboard() {
        view?.let {
            activity?.hideKeyboard(it)
        }
    }

    private fun setupRecyclerViewPaymentMethod(paymentMethods: ArrayList<PaymentMethodItem>) {
        paymentMethodListAdapter =
            PaymentMethodListAdapter(::onPaymentMethodClick, ::onCheckPaymentMethodClick)
        listPaymentMethod.adapter = paymentMethodListAdapter
        paymentMethodListAdapter.setPaymentMethods(paymentMethods)
    }

    private fun onPaymentMethodClick(paymentMethod: PaymentMethodItem) {
        sharedViewModel.paymentMethods.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {

                val paymentMethodItem = it.find { p -> p.id == paymentMethod.id }

                if (paymentMethodItem != null) {
                    sharedViewModel.setPaymentMethod(paymentMethodItem)
                }

            }
        })
    }

    private fun onCheckPaymentMethodClick(paymentMethods: ArrayList<PaymentMethodItem>) {
        sharedViewModel.setPaymentMethods(paymentMethods)
        val paymentMethodItemCash = paymentMethods.find { p -> p.id == 1 }
        if (paymentMethodItemCash == null) {
            sharedViewModel.cashAmount = 0.0
            sharedViewModel.deduct()
        }
        val paymentMethodItemMomo = paymentMethods.find { p -> p.id == 2 }
        if (paymentMethodItemMomo == null) {
            sharedViewModel.mobileMoneyAmount = 0.0
            sharedViewModel.deduct()
        }
        val paymentMethodItemVisa = paymentMethods.find { p -> p.id == 3 }
        if (paymentMethodItemVisa == null) {
            sharedViewModel.visaAmount = 0.0
            sharedViewModel.deduct()
        }
    }

    private fun setupRecyclerViewDiscountType(discountTypes: ArrayList<DiscountTypesItem>) {
        val discountListAdapter =
            DiscountListAdapter(::onDiscountTypeClick)
        listDiscountType.adapter = discountListAdapter
        discountListAdapter.setDiscountTypes(discountTypes)
    }

    private fun onDiscountTypeClick(discountType: DiscountTypesItem) {
//        sharedViewModel.setDiscountType(discountType)
        showDialog(discountType)
    }

    private fun showDialog(discountType: DiscountTypesItem) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_authentication, null)

        dialogView.progressBarDialogAuth.visibility = View.GONE

        viewModel.loginFormState.observe(
            viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                dialogView.buttonDialogAuthApprove.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    dialogView.editTextDialogAuthUsername.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    dialogView.editTextDialogAthPassword.error = getString(it)
                }
            })


        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.loginDataChanged(
                    dialogView.editTextDialogAuthUsername.text.toString(),
                    dialogView.editTextDialogAthPassword.text.toString()
                )
            }
        }

        dialogView.editTextDialogAuthUsername.addTextChangedListener(afterTextChangedListener)
        dialogView.editTextDialogAthPassword.addTextChangedListener(afterTextChangedListener)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Approval for Discount ${discountType.name} ${discountType.percentageStr}")

        val alertDialog = builder.show()

        viewModel.loginResult.observe(
            viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                dialogView.progressBarDialogAuth.visibility = View.GONE

                result.getContentIfNotHandled()?.let { loginResult ->
                    loginResult.error?.let {
                        showLoginFailed(it)
//                        sharedViewModel.logout()
                    }
                    loginResult.success?.let { user ->
//                        sharedViewModel.authenticate()
                        if (isUserSupervisor(user)) {
                            alertDialog.dismiss()
                            sharedViewModel.setDiscountType(discountType)
                        } else {
                            alertDialog.dismiss()
                            Toast.makeText(context, "Unauthorized User", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            })


        dialogView.buttonDialogAuthApprove.setOnClickListener {

            dialogView.progressBarDialogAuth.visibility = View.VISIBLE

            val username = dialogView.editTextDialogAuthUsername.text.toString()
            val password = dialogView.editTextDialogAthPassword.text.toString()

            viewModel.usernameLogin = username
            viewModel.passwordLogin = password

            ldIn()
        }

        dialogView.buttonDialogAuthCancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun ldIn() {
        val result = viewModel.login
        result.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            it.let {

                viewModel.updateLoginResult(it)

            }
        })
    }

    private fun showTransactionFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun isUserSupervisor(model: LoggedInUser): Boolean {
        return model.userRoles.any { x -> x.role.name.toLowerCase(Locale.ROOT) == "supervisor" }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
