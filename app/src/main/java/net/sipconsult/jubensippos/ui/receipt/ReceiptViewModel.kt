package net.sipconsult.jubensippos.ui.receipt

import androidx.lifecycle.ViewModel
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepository
import net.sipconsult.jubensippos.util.BluetoothUtil
import net.sipconsult.jubensippos.util.Receipt
import net.sipconsult.jubensippos.util.SunmiPrintHelper

class ReceiptViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    lateinit var receipt: Receipt

    private val isBold = true
    private val isUnderLine: Boolean = false

    fun printReceipt() {
        if (!BluetoothUtil.isBlueToothPrinter) {
//            SunmiPrintHelper.instance.printBitmap()
            SunmiPrintHelper.instance.printText(receipt.receiptPreview, 24.0F, isBold, isUnderLine)
//            SunmiPrintHelper.instance.feedPaper()
            SunmiPrintHelper.instance.cutpaper()
        }
    }


}