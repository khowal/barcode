package com.example.barcode.Screens

import ApiController
import Commons.Companion.showToast
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import coil.load
import com.example.barcode.BaseActivity
import com.example.barcode.R
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors


class ScannerScreen : BaseActivity() {


    private lateinit var previewView: PreviewView
    private lateinit var img_close: ImageView

    var isScanning: Boolean = true;
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    lateinit var builder: AlertDialog.Builder

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            builder.setMessage(R.string.cemara_permission_msg)
            builder.setPositiveButton(R.string.setting) { dialogInterface, which ->
                finish()
                val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
            }

            builder.setNegativeButton(R.string.exit) { dialogInterface, which ->
                dialogInterface.dismiss()
                finish()
            }

            builder.setNeutralButton(R.string.retry) { dialogInterface, which ->
                setupScanner();
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()


        }
    }

    fun setupScanner() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qr_code_scanner)
        previewView = findViewById(R.id.previewView)
        img_close = findViewById(R.id.img_close)
        builder = AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)

        setupScanner();


        img_close.setOnClickListener {
            finish()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Set up the ImageAnalysis use case to scan frames
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // Set up the Barcode Scanner to scan both QR codes and barcodes
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE,   // For QR Code
                    Barcode.FORMAT_CODE_128,  // For Code 128 barcodes
                    Barcode.FORMAT_EAN_13,    // For EAN-13 barcodes
                    Barcode.FORMAT_UPC_A,     // For UPC-A barcodes
                    Barcode.FORMAT_EAN_8,     // For EAN-8 barcodes
                    Barcode.FORMAT_UPC_E,     // For UPC-E barcodes
                    Barcode.FORMAT_CODE_39,   // For Code 39 barcodes
                    Barcode.FORMAT_ITF,       // For ITF barcodes
                    Barcode.FORMAT_CODABAR    // For Codabar barcodes
                )
                .build()
            val scanner = BarcodeScanning.getClient(options)

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(scanner, imageProxy)
            }

            // Bind the preview and analysis use cases to the lifecycle
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("Scanner Activity", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                isScanning = true
            }

            Activity.RESULT_CANCELED -> {
                isScanning = true
            }

            else -> {
                isScanning = true
            }
        }
    }


    fun moveToDetailScreen(barcodeValue : String){
        val intent = Intent(this, DataSubmitWithBarcode::class.java)
        intent.putExtra(AppStrings.INTENT_BARCODE, barcodeValue)
        someActivityResultLauncher.launch(intent)
    }



    private fun processImageProxy(
        scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { barcodeValue ->

                            Log.d("BarcodeScanActivity", "Barcode/QR Code Scanned: $barcodeValue")

                            if (isScanning) {
                                isScanning = false


                                moveToDetailScreen(barcodeValue)

//

//                                isScanning = true
//                                builder.setMessage(barcodeValue)
//                                builder.setIcon(R.mipmap.ic_launcher)
//
//                                builder.setPositiveButton(R.string.retry) { dialogInterface, which ->
//                                    dialogInterface.dismiss()
//                                    isScanning = true
//                                }
//
//                                builder.setNegativeButton(R.string.exit) { dialogInterface, which ->
//                                    dialogInterface.dismiss()
//                                    finish()
//                                }
//
//                                val alertDialog: AlertDialog = builder.create()
//                                alertDialog.setCancelable(false)
//                                alertDialog.show()


                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("BarcodeScanActivity", "Barcode scanning failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}