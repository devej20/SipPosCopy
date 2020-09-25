package net.sipconsult.jubensippos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scanning.*


class ScanningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)

        editTextBarcode.isFocusableInTouchMode = true
        editTextBarcode.isFocusable = true
        editTextBarcode.requestFocus()
        editTextBarcode.inputType = InputType.TYPE_NULL

        buttonBarcodeEnter.setOnClickListener {
            val productBarcode = editTextBarcode.text.toString()
            doneWith(productBarcode)
        }

        editTextBarcode.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val productBarcode = editTextBarcode.text.toString()
                doneWith(productBarcode)
                return@OnKeyListener true
            }
            false
        })


    }

    private fun showKeyboard() {
        editTextBarcode.inputType = InputType.TYPE_CLASS_TEXT
        editTextBarcode.requestFocus()
        val mgr: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(editTextBarcode, InputMethodManager.SHOW_FORCED)
    }

    private fun doneWith(productBarcode: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("product_barcode", productBarcode)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}