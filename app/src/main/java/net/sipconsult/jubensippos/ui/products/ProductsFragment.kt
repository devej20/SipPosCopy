package net.sipconsult.jubensippos.ui.products

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.jubensippos.R
import net.sipconsult.jubensippos.data.models.CartItem
import net.sipconsult.jubensippos.data.models.ProductItem
import net.sipconsult.jubensippos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.jubensippos.ui.base.ScopedFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


class ProductsFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactory: ProductViewModelFactory by instance()

    private lateinit var viewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ProductsViewModel::class.java)

        bindUI()
    }

    private fun bindUI() = launch {
        val locations = viewModel.locations.await()

        locations.observe(viewLifecycleOwner, Observer { lcs ->
            if (lcs == null) return@Observer
            val size = lcs.size
//            Toast.makeText(context, "Locations size $size",Toast.LENGTH_SHORT).show()

        })
        val products = viewModel.products.await()
        products.observe(viewLifecycleOwner, Observer { pdts ->
            if (pdts == null) return@Observer
            groupLoadingProducts.visibility = View.GONE
            setupRecyclerView(pdts as ArrayList<ProductItem>)
            viewModel.productItems = pdts
        })

        buttonScanBarcode.setOnClickListener {
//            Toast.makeText(activity, "Scan", Toast.LENGTH_LONG).show()
            startScanSunmi()
        }

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

    private fun setupSearchView(productListAdapter: ProductListAdapter) {
        searchProducts.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                productListAdapter.filter.filter(newText)
                return false
            }

        })
    }

    private fun setupRecyclerView(products: ArrayList<ProductItem>) {
        val productRecyclerAdapter =
            ProductListAdapter(::onProductClick)
        listProducts.adapter = productRecyclerAdapter
        productRecyclerAdapter.setProducts(products)
        setupSearchView(productRecyclerAdapter)
    }

    private fun onProductClick(product: ProductItem) {
        viewModel.addCartItem(product)
    }

    private fun startScanZxing() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setPrompt("Scan a barcode")
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    private fun startScanSunmi() {
        val intent = Intent("com.summi.scan")

        intent.setPackage("com.sunmi.sunmiqrcodescanner")

        intent.putExtra(
            "CURRENT_PPI",
            0X0003
        )//The current preview resolution ,PPI_1920_1080 = 0X0001;PPI_1280_720 = 0X0002;PPI_BEST = 0X0003;

        intent.putExtra("PLAY_SOUND", true)// Prompt tone after scanning  ,default true

        intent.putExtra(
            "PLAY_VIBRATE",
            false
        )//vibrate after scanning,default false,only support M1 right now.

        intent.putExtra("IDENTIFY_INVERSE_QR_CODE", true)//Whether to identify inverse code

        intent.putExtra(
            "IDENTIFY_MORE_CODE",
            false
        )// Whether to identify several code，default false

        intent.putExtra(
            "IS_SHOW_SETTING",
            true
        )// Wether display set up button  at the top-right corner，default true

        intent.putExtra("IS_SHOW_ALBUM", true)// Wether display album，default true


        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && data != null) {
            val bundle = data.extras
            val result = bundle!!.getSerializable("data") as ArrayList<HashMap<String, String>>

            val it: MutableIterator<Any>? = result.iterator()

            while (it!!.hasNext()) {
                val hashMap = it.next() as HashMap<String, String>

                val scanType = hashMap["TYPE"]!!
                val scanResult = hashMap["VALUE"]!!

                Log.i("sunmi_scanner_type: ", scanType)//this is the type of the code
//                Toast.makeText(activity, "Scan Type：$scanType", Toast.LENGTH_SHORT).show()

                Log.i("sunmi_scanner_result: ", scanResult)//this is the result of the code
//                Toast.makeText(activity, "Results：$scanResult", Toast.LENGTH_SHORT).show()

                val barcode: String = scanResult
                addScannedCartItem(barcode)

            }
        }
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                scanResult(result)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun scanResult(result: IntentResult?) {
        Toast.makeText(activity, "Scanned: " + result!!.contents, Toast.LENGTH_SHORT).show()
        val barcode: String = result.contents
        addScannedCartItem(barcode)
    }

    private fun addScannedCartItem(barcode: String) {
        val pdt = viewModel.productItems.find { p -> p.barcode == barcode }
        if (pdt != null) {
            val cartItem = CartItem(pdt)
            cartItem.let { it.let { it1 -> ShoppingCartRepository.addCartItem(it1) } }
            ShoppingCartRepository.addCartItem(cartItem)
            Toast.makeText(activity, "Product found", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Product not found", Toast.LENGTH_SHORT).show()
        }

    }


}