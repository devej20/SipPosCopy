package net.sipconsult.jubensippos

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.sipconsult.jubensippos.data.models.*
import net.sipconsult.jubensippos.data.network.response.TransactionResponse
import net.sipconsult.jubensippos.data.provider.LocationProvider
import net.sipconsult.jubensippos.data.repository.discountType.DiscountTypeRepository
import net.sipconsult.jubensippos.data.repository.location.LocationRepository
import net.sipconsult.jubensippos.data.repository.paymentMethod.PaymentMethodRepository
import net.sipconsult.jubensippos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepository
import net.sipconsult.jubensippos.data.repository.user.UserRepository
import net.sipconsult.jubensippos.internal.Event
import net.sipconsult.jubensippos.internal.Result
import net.sipconsult.jubensippos.internal.lazyDeferred
import net.sipconsult.jubensippos.ui.login.AuthenticationState
import net.sipconsult.jubensippos.ui.payment.TransactionResult
import net.sipconsult.jubensippos.ui.payment.paymentmethod.loyalty.VoucherResult
import net.sipconsult.jubensippos.util.Receipt
import net.sipconsult.jubensippos.util.ReceiptBuilder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class SharedViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val discountTypeRepository: DiscountTypeRepository,
    private val locationProvider: LocationProvider,
    private val locationRepository: LocationRepository,
    val context: Context
) : ViewModel() {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("AndroidHiveLogin", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPref.edit()

    //    lateinit var user: LoggedInUser
    private val locationId = locationProvider.getLocation()

    private var dateNow = Date()
    private var receiptNumber: String = ""
    private var total: Double = 0.0
    lateinit var receipt: Receipt
    private val decimalFormater = DecimalFormat("0.00")
    private var username: String = ""
    var email: String = ""
    var voucherCode: String = ""

    private var isReceiptNumberGenerated: Boolean = false
    var isPrintReceipt: Boolean = false
    var isDeliveryCostAdd: Boolean = false
    var isDeliveryCostSub: Boolean = false

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState

    private val _user = MutableLiveData<LoggedInUser>()
    val user: LiveData<LoggedInUser> = _user

    val cartItems: LiveData<MutableList<CartItem>> = ShoppingCartRepository.cartItems
    val location: LocationsItem = locationRepository.getLocation(locationId)

    val totalCartPrice: LiveData<Double> = ShoppingCartRepository.totalPrice

    private var _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice
    private var _totalDiscountPrice = MutableLiveData<Double>()
    var totalDiscountPrice = _totalDiscountPrice

    val totalQuantity: LiveData<Int> = ShoppingCartRepository.totalQuantity

    private val _paymentMethods = MutableLiveData<ArrayList<PaymentMethodItem>>()
    val paymentMethods: LiveData<ArrayList<PaymentMethodItem>> = _paymentMethods

    private val _selectedPaymentMethod = MutableLiveData<PaymentMethodItem>()
    val selectedPaymentMethod: LiveData<PaymentMethodItem> = _selectedPaymentMethod

    val editTextDeliveryCost = MutableLiveData<String>()
    var deliveryCost: Double = 0.0
    private val _discountType = MutableLiveData<DiscountTypesItem>()
    val discountType: LiveData<DiscountTypesItem> = _discountType

    private val _salesAgent = MutableLiveData<SalesAgentsItem>()
    val salesAgent: LiveData<SalesAgentsItem> = _salesAgent

    val _voucherResult = MutableLiveData<VoucherResult>()
    val voucherResult: LiveData<VoucherResult> = _voucherResult


    private val _transactionResult = MutableLiveData<Event<TransactionResult>>()
    val transactionResult: LiveData<Event<TransactionResult>> = _transactionResult

    val editTextTender = MutableLiveData<String>()
    var cashAmount: Double = 0.0

    val editTextMobileMoneyPhoneNumber = MutableLiveData<String>()
    var mobileMoneyPhoneNumber: String = ""
    var editTextMobileMoneyAmount = MutableLiveData<String>()
    var mobileMoneyAmount: Double = 0.0

    val editTextCardNumber = MutableLiveData<String>()
    var cardNumber: String = ""
    val editTextCardAmount = MutableLiveData<String>()
    var cardAmount: Double = 0.0

    val editTextChequeNumber = MutableLiveData<String>()
    var chequeNumber: String = ""
    val editTextChequeAmount = MutableLiveData<String>()
    var chequeAmount: Double = 0.0

    var totalAmount: Double = 0.0

    private val _change = MutableLiveData<String>()
    val change: LiveData<String> = _change

    init {

        if (userRepository.isLoggedIn())
        // In this example, the user is always unauthenticated when MainActivity is launched
            _authenticationState.value = AuthenticationState.AUTHENTICATED
        else
            _authenticationState.value = AuthenticationState.UNAUTHENTICATED

        if (userRepository.isLoggedIn()) {
            userRepository.loggedInUser.observeForever {
                _user.value = it
                username = it.displayName
                email = it.email
            }
        }

        _totalPrice.postValue(ShoppingCartRepository.totalCartPrice)
    }

    fun setTotalPrice() {
        _totalPrice.postValue(ShoppingCartRepository.totalCartPrice)
    }

    fun logout() {
        userRepository.logout()
        _authenticationState.postValue(AuthenticationState.UNAUTHENTICATED)
    }

    fun authenticate() {
        _authenticationState.value = AuthenticationState.AUTHENTICATED
    }


    fun deduct() {
        val totalAmount = cashAmount + mobileMoneyAmount + cardAmount
        val change: Double = totalAmount - totalPrice.value!!
        _change.value = decimalFormater.format(change)

    }

    fun addDeliveryCost() {
        val newTotalPrice: Double = totalPrice.value!! + deliveryCost
        _totalPrice.postValue(decimalFormater.format(newTotalPrice).toDouble())
    }

    fun subDeliveryCost() {
        val newTotalPrice: Double = totalPrice.value!! - deliveryCost
        _totalPrice.postValue(decimalFormater.format(newTotalPrice).toDouble())
    }


    fun setPaymentMethods(paymentMethods: ArrayList<PaymentMethodItem>) {
        _paymentMethods.value = paymentMethods
    }

    fun setPaymentMethod(paymentMethod: PaymentMethodItem) {
        _selectedPaymentMethod.value = paymentMethod
    }

    fun setDiscountType(discountType: DiscountTypesItem) {
        _discountType.value = discountType
    }

    fun setSalesAgent(salesAgent: SalesAgentsItem) {
        _salesAgent.value = salesAgent
    }

    val getPaymentMethods by lazyDeferred {
        paymentMethodRepository.getPaymentMethods()
    }

    val discountTypes by lazyDeferred {
        discountTypeRepository.getDiscountTypes()
    }

    val getVoucher by lazyDeferred {

        paymentMethodRepository.getVoucher(voucherCode)

    }

    private fun compareToDay(date1: Date?, date2: Date?): Int {
        if (date1 == null || date2 == null) {
            return 0
        }
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(date1).compareTo(sdf.format(date2))
    }

    private val generateReceiptNumber: String
        get() {

//            var receiptNumber = ""
            val dateNow = Date(System.currentTimeMillis())

            val millis = dateNow.time

            val currentDate: Long = sharedPref.getLong(KEY_CURRENT_DATE, 0L)
            var currentIndex: Long = sharedPref.getLong(KEY_CURRENT_INDEX, -1L)
            if (currentDate == 0L)
                editor.putLong(KEY_CURRENT_DATE, millis).apply()

            val myCurrentDate = Date(sharedPref.getLong(KEY_CURRENT_DATE, 0))

            val formatReceiptNumber = SimpleDateFormat("yyMMdd", Locale.getDefault())
            val dateToStr = formatReceiptNumber.format(dateNow)

            val userInitial: String = user.value!!.abbrv

            if (compareToDay(dateNow, myCurrentDate) == 0) {
                if (currentIndex == -1L) {
                    editor.putLong(KEY_CURRENT_INDEX, 1).apply()
                }
                currentIndex = sharedPref.getLong(KEY_CURRENT_INDEX, 0L)

                receiptNumber = String.format("%s%s%o", userInitial, dateToStr, currentIndex)

                val newIndex = currentIndex + 1
                editor.putLong(KEY_CURRENT_INDEX, newIndex).apply()

            } else {

                editor.putLong(KEY_CURRENT_DATE, millis).apply()
                if (currentIndex == -1L) {
                    editor.putLong(KEY_CURRENT_INDEX, 1).apply()
                }

                editor.putLong(KEY_CURRENT_INDEX, 1).apply()
                currentIndex = sharedPref.getLong(KEY_CURRENT_INDEX, 0L)

                receiptNumber = String.format("%s%s%o", userInitial, dateToStr, currentIndex)

                val newIndex = currentIndex + 1
                editor.putLong(KEY_CURRENT_INDEX, newIndex).apply()
            }

            return receiptNumber
        }

    fun generateReceiptNumber() {
        if (!isReceiptNumberGenerated) {
            receiptNumber = generateReceiptNumber
            isReceiptNumberGenerated = true
        }

    }

    fun updateTotalPrice(newTotalPrice: Double) {
        _totalPrice.postValue(
            decimalFormater.format(newTotalPrice).toDouble()
        )
    }

    fun updateDiscountPrice(discountPrice: Double) {
        _totalDiscountPrice.postValue(
            decimalFormater.format(discountPrice).toDouble()
        )

    }

    private fun calVAT(): String {
        val vatDiscount = 0.03
        val vatAmount: Double = vatDiscount.times(total)
        val newTotalPrice: Double = total - vatAmount
        return decimalFormater.format(vatAmount)

    }

    private fun calSubTotal(): String {
        val vatDiscount = 0.03
        val vatAmount: Double = vatDiscount.times(total)
        val subTotal: Double = total - (vatAmount + deliveryCost)
        return decimalFormater.format(subTotal)

    }

    private fun paymentMethodsStr(): String {
        val paymentMethods = paymentMethods.value!!

        val sb = StringBuilder()

//        for (i in 0 until paymentMethods.size) {
        for (str in paymentMethods) {
            val prefix = ", "
            sb.append(prefix)
            sb.append(str.name.trim())
        }
//        }

        return sb.toString()

    }

    private fun receiptPaymentMethod(): ArrayList<PaymentMethodItem> {
        val paymentMethodItems = arrayListOf<PaymentMethodItem>()
        for (pm in paymentMethods.value!!) {
            lateinit var paymentMethodItem: PaymentMethodItem
            when (pm.id) {
                1 -> {
                    pm.amountPaid = cashAmount
                    pm.displayName = "Cash Amount Paid"
                    paymentMethodItem = pm
                }
                2 -> {
                    pm.amountPaid = mobileMoneyAmount
                    pm.displayName = "Mobile Money Amount Paid"
                    paymentMethodItem = pm
                }
                3 -> {
                    pm.amountPaid = cardAmount
                    pm.displayName = "Card Amount Paid"
                    paymentMethodItem = pm
                }
            }
            paymentMethodItems.add(paymentMethodItem)
        }

        return paymentMethodItems
    }

    fun buildReceipt() {
        val items = cartItems.value!!
        total = totalPrice.value!!.toDouble()
        val dateStr = DateFormat.format("dd/MM/yyyy", dateNow).toString()
        val timeStr = DateFormat.format("HH:mm:ss", dateNow).toString()
        val totalString = decimalFormater.format(total)
        val locationR = locationRepository.getLocation(locationId)
        val userR = user.value!!
        val salesAgentR = salesAgent.value!!
        val deliveryCostR = deliveryCost
        val subTotal = calSubTotal()
        val vat = calVAT()
        val paymentMethodsR = receiptPaymentMethod()
        val paymentMethodsSR = paymentMethodsStr()

        receipt =
            if (discountType.value != null) {
                ReceiptBuilder()
                    .header("JUBEN HOUSE OF BEAUTY")
                    .text(locationR.address)
                    .text("Tel: ${locationR.telephone}")
                    .text("Mobile: ${locationR.mobileNumber1}")
                    .text("WhatsApp: ${locationR.mobileNumber2}")
                    .text("Tin: C0005355370")
                    .subHeader("SALES RECEIPT")
                    .divider()
                    .text("Date: $dateStr")
                    .text("Time: $timeStr")
                    .text("Receipt No: $receiptNumber")
                    .text("Operator: ${userR.displayName}")
                    .text("Sales Agent: ${salesAgentR.displayName}")
                    .text("Shop: ${locationR.name}")
                    .divider()
                    .subHeader("Items")
                    .menuItems(items)
                    .dividerDouble()
                    .menuLine("SubTotal", "GHC $subTotal")
                    .menuLine("3% VAT Rate", "GHC $vat")
                    .menuLine(
                        "Discount ${discountType.value!!.percentageStr}",
                        "GHC ${totalDiscountPrice.value!!}"
                    )
                    .menuLine("Delivery Cost", "GHC $deliveryCostR")
                    .menuLine("Total", "GHC $totalString")
                    .menuPaymentMethod(paymentMethodsR)
                    .menuLine("Change", "GHC ${change.value}")
                    .menuLine("Payment Method", paymentMethodsSR)
                    .dividerDouble()
                    .stared("THANK YOU")
                    .build()
            } else {
                ReceiptBuilder()
                    .header("JUBEN HOUSE OF BEAUTY")
                    .text(locationR.address)
                    .text("Tel: ${locationR.telephone}")
                    .text("Mobile: ${locationR.mobileNumber1}")
                    .text("WhatsApp: ${locationR.mobileNumber2}")
                    .text("Tin: C0005355370")
                    .subHeader("SALES RECEIPT")
                    .divider()
                    .text("Date: $dateStr")
                    .text("Time: $timeStr")
                    .text("Receipt No: $receiptNumber")
                    .text("Operator: ${userR.displayName}")
                    .text("Sales Agent: ${salesAgentR.displayName}")
                    .text("Shop: ${locationR.name}")
                    .divider()
                    .subHeader("Items")
                    .menuItems(items)
                    .dividerDouble()
                    .menuLine("SubTotal", "GHC $subTotal")
                    .menuLine("3% VAT Rate", "GHC $vat")
                    .menuLine("Delivery Cost", "GHC $deliveryCostR")
                    .menuLine("Total", "GHC $totalString")
                    .menuPaymentMethod(paymentMethodsR)
                    .menuLine("Change", "GHC ${change.value}")
                    .menuLine("Payment Method", paymentMethodsSR)
                    .dividerDouble()
                    .stared("THANK YOU")
                    .build()
            }
    }


    val postTransaction by lazyDeferred {

        val locationP = locationRepository.getLocation(locationId)
        val products = arrayListOf<SalesTransactionPostProduct>()
        for (item in cartItems.value!!) {
            val product = SalesTransactionPostProduct(
                productCode = item.product.code,
                quantity = item.quantity
            )
            products.add(product)
        }

        val paymentMethods = arrayListOf<SalesTransactionPostPaymentMethod>()
        for (paymentMethod in _paymentMethods.value!!) {
            lateinit var salesTransactionPostPaymentMethod: SalesTransactionPostPaymentMethod
            when (paymentMethod.id) {
                1 -> {
                    salesTransactionPostPaymentMethod = if (totalPrice.value!! > cashAmount) {
                        SalesTransactionPostPaymentMethod(
                            paymentMethodId = paymentMethod.id,
                            amount = cashAmount
                        )
                    } else {
                        SalesTransactionPostPaymentMethod(
                            paymentMethodId = paymentMethod.id,
                            amount = (cashAmount - change.value!!.toDouble())
                        )
                    }

                }
                2 -> {
                    salesTransactionPostPaymentMethod = SalesTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = mobileMoneyAmount
                    )
                }
                3 -> {
                    salesTransactionPostPaymentMethod = SalesTransactionPostPaymentMethod(
                        paymentMethodId = paymentMethod.id,
                        amount = cardAmount
                    )
                }
            }
            paymentMethods.add(salesTransactionPostPaymentMethod)
        }

        val body = SaleTransactionPostBody(
            locationCode = locationP.code,
            operatorUserId = user.value!!.id,
            receiptNumber = receiptNumber,
            salesAgentUserId = salesAgent.value!!.id,
            salesTransactionProduct = products,
            salesTransactionPaymentMethod = paymentMethods
        )

        if (discountType.value != null) {
            body.discountTypeId = discountType.value?.id
        }

        if (editTextDeliveryCost.value != null && deliveryCost > 0.0) {
            body.deliveryCost = deliveryCost
        }

        transactionRepository.postTransaction(body)
    }

    fun updateTransactionResult(result: Result<TransactionResponse>) {

        _transactionResult.value = if (result is Result.Success) {
            Event(TransactionResult(success = result.data.successful))
        } else {
            Event(TransactionResult(error = R.string.transaction_failed))
        }

    }
/*

    fun saveSaleTransaction(
//        salesTransaction: SalesTransactionItem,
//        products: List<Product>,
//        client: Client,
//        paymentMethod: PaymentMethod,
//        location: Location,
//        operator: Operator
    ) {
        val salesTransactionItem = SalesTransactionItem(
            date = dateNow,
            receiptNumber = receiptNumber,
            totalPrice = total,
            totalQuantity = totalQuantity.value!!,
            status = 1

        )
        val products = arrayListOf<Product>()
        for (item in cartItems.value!!) {
            val product = Product(
                categoryId = item.product.categoryId,
                description = item.product.description,
                imageUrl = item.product.imageUrl,
                name = item.product.name,
                salePrice = item.product.salePrice,
                costPrice = item.product.costPrice,
                barcode = item.product.barcode,
                quantity = item.quantity
            )
            products.add(product)
        }
        val paymentMethod = PaymentMethod(
            code = paymentMethod.value!!.code,
            name = paymentMethod.value!!.name
        )
        val client = Client(
            code = "",
            firstName = "",
            lastName = "",
            phoneNumber1 = "",
            phoneNumber2 = "",
            email = "",
            description = "",
            address = ""

        )
        val location = Location(
            code = "",
            name = ""

        )
        val operator = Operator(
            userId = user.id,
            firstName = user.firstName,
            lastName = user.lastName

        )
        transactionRepository.saveSalesTransaction(
            salesTransactionItem,
            products,
            client,
            paymentMethod,
            location,
            operator
        )
    }
*/

    fun resetAll() {
        ShoppingCartRepository.removeALLCartItem()
        _paymentMethods.postValue(null)
        _discountType.postValue(null)
        _salesAgent.postValue(null)
        _transactionResult.postValue(null)
        editTextTender.postValue(null)
        cashAmount = 0.0
        mobileMoneyAmount = 0.0
        cardAmount = 0.0
        totalAmount = 0.0
        isReceiptNumberGenerated = false
        isPrintReceipt = false
    }

    fun resetPaymentMethodAndDiscount() {
        _paymentMethods.postValue(null)
        _discountType.postValue(null)
        _salesAgent.postValue(null)

    }

    fun resetTransaction() {
        _transactionResult.postValue(null)
    }

    fun updatePaymentMethods(paymentMethods: ArrayList<PaymentMethodItem>) {
        _paymentMethods.value = paymentMethods
    }

    companion object {
        private const val TAG: String = "SharedViewModel"
        private const val PREF_NAME = "AndroidHiveLogin"

        private const val KEY_CURRENT_DATE = "current_date"
        private const val KEY_CURRENT_INDEX = "current_index"
    }

}