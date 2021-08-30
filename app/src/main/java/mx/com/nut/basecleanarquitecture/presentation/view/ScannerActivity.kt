package mx.com.nut.basecleanarquitecture.presentation.view

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.android.synthetic.main.activity_scanner.*

class ScannerActivity: BaseActivity() {

    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        btnBack.setOnClickListener {
            onBackPressed()
        }

        //Initialize barcode scanner view
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)

        //start capture
        capture = CaptureManager(this, barcodeScannerView)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.decode()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_scanner
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture!!.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcodeScannerView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
}