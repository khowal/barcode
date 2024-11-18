package com.example.barcode.Screens

import ApiController
import AppStrings
import Commons
import Commons.Companion.arePermissionsGranted
import Commons.Companion.askForNotificationPermission
import Commons.Companion.initBgProcess
import Commons.Companion.showToast
import MyDatabaseHelper
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.barcode.BaseActivity
import com.example.barcode.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataSubmitWithBarcode : BaseActivity() {


    var barcodeValue = ""
    private lateinit var imgUser: ImageView
    private lateinit var rlProgressView: RelativeLayout
    private lateinit var liDataView: LinearLayout
    private lateinit var txtUserName: TextView
    private lateinit var btnSubmit: Button
    private lateinit var btnImageSelect: Button
    private lateinit var li_selectedImag: LinearLayout
    private lateinit var imgSelected: ImageView

    //
    var userName = ""

    //
    var apiController: ApiController = ApiController()

    //
    var clickedSelection = 0; // 0 -> camera , 1 -> gallery

    //
    private var photoFile: File? = null

    //
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private fun showImageSourceDialog() {
        val options = arrayOf(
            getString(R.string.camera),
            getString(R.string.gallery),
        )

        AlertDialog.Builder(this).setTitle(getString(R.string.select_image_source))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        clickedSelection = 0;
                        openCamera()
                    }

                    1 -> {
                        openGallery()
                        clickedSelection = 1;
                    }

                }
            }.show()
    }


    val permissionStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionCamera = Manifest.permission.CAMERA;

    private var REQUIRED_PERMISSIONS: Array<String> = arrayOf()

    private fun openCamera() {
        REQUIRED_PERMISSIONS = arrayOf(
            permissionCamera
        )

        if (arePermissionsGranted(this, REQUIRED_PERMISSIONS)) {
            // Create file in external storage directory
            photoFile = createImageFileInExternalStorage()
            photoFile?.let { file ->
                val photoURI: Uri = FileProvider.getUriForFile(
                    this, "$packageName.provider", file
                )
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
                cameraLauncher.launch(intent)
            }
        } else {
            permissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val selectedUri = data?.data // Single selection
                val clipData = data?.clipData // Multiple selection

                if (selectedUri != null) {
                    photoFile = saveUriToTempFile(selectedUri)
                    photoFile?.let {
                        li_selectedImag.visibility = View.VISIBLE
                        imgSelected.setImageURI(Uri.fromFile(it))
                        photoFile = it
                    } ?: run {
                        showToast(this, "Error saving image")
                    }

                } else if (clipData != null) {
//                    for (i in 0 until clipData.itemCount) {
//                        handleUri(clipData.getItemAt(i).uri)
//                    }
                }
            }
        }

    private fun saveUriToTempFile(uri: Uri): File? {
        return try {

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.app_name)
            )
            if (!storageDir.exists()) storageDir.mkdirs()

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("IMG_$timestamp", ".jpeg", storageDir)

            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Initialize the gallery launcher
    var openGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                photoFile = saveUriToTempFile(uri)
                photoFile?.let {
                    li_selectedImag.visibility = View.VISIBLE
                    imgSelected.setImageURI(Uri.fromFile(it))
                    photoFile = it
                } ?: run {
                    showToast(this, "Error saving image")
                }
                Log.e("1111111111", "Galleryyyyy>>> ${photoFile?.path}")

            } else {
                showToast(this, "No image selected");
            }
        }

    private fun openGallery() {
        REQUIRED_PERMISSIONS = arrayOf(
            permissionStorage
        )

//        if (arePermissionsGranted(this, REQUIRED_PERMISSIONS)) {
//            openGalleryLauncher.launch("image/*")

        val intent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
            type = "image/*"
        }
        pickMediaLauncher.launch(intent)
//        } else {
//            permissionLauncher.launch(REQUIRED_PERMISSIONS)
//        }
    }


    private fun createImageFileInExternalStorage(): File? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.app_name)
        )
        if (!storageDir.exists()) storageDir.mkdirs()

        return File(storageDir, "IMG_$timestamp.jpeg")
    }


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                photoFile?.let { file ->
                    li_selectedImag.visibility = View.VISIBLE
                    imgSelected.setImageURI(Uri.fromFile(file))
                }
            } else {
                photoFile = null;
            }
        }

    // Initialize the permission request launcher
    val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Check the results of the permissions request
            val allPermissionsGranted = permissions.values.all { it == true }
            if (allPermissionsGranted) {
                if (clickedSelection == 0) {
                    openCamera();
                }

                if (clickedSelection == 1) {
                    openGallery();
                }


            } else {

                showToast(this, "Permissions denied")
            }
        }

    fun submitImage() {
        if (photoFile != null) {


            val data = mapOf(
                AppStrings.PARAM_ROLL_NUMBER to barcodeValue,
                AppStrings.PARAM_PHOTO to photoFile!!,
                AppStrings.PARAM_NAME to userName,
            )
            apiController.uploadBarcodeInfo(this, data, { successResonse ->

                Commons.showDialog(this, successResonse ?: "", {
                    finish()
                })

            }, { failureResponse ->
                Commons.showDialog(this, failureResponse ?: "", {})

            }, {

                Commons.optionDialog(this,
                    "Intenet is not available, You can turn on internet and retry the process or save data into local file and Application will upload data once intenet will available again!",
                    getString(R.string.save),
                    getString(R.string.retry),
                    getString(R.string.cancel),
                    {

                        askForNotificationPermission(this, {
                            registerBgService()
                        }, {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        })
                    },
                    {
                        submitImage();
                    })


            }, true)
        } else {
            showToast(this, "Please Select a file")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_submit_with_barcode)


        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                registerBgService()
            } else {
                showToast(this, "Notification permission denied")
            }
        }

        imgUser = findViewById(R.id.imgUser)
        rlProgressView = findViewById(R.id.rlProgressView)
        liDataView = findViewById(R.id.liDataView)
        txtUserName = findViewById(R.id.txtUserName)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnImageSelect = findViewById(R.id.btnImageSelect)
        li_selectedImag = findViewById(R.id.li_selectedImag)
        li_selectedImag.visibility = View.GONE
        imgSelected = findViewById(R.id.imgSelected)


        btnSubmit.setOnClickListener {
            submitImage();
        }


        //
        rlProgressView.visibility = View.VISIBLE
        liDataView.visibility = View.GONE

        barcodeValue = intent.getStringExtra(AppStrings.INTENT_BARCODE) ?: "";
        val data = mapOf(
            AppStrings.PARAM_ROLL_NUMBER to barcodeValue
        )

        apiController.fetchBarcodeInfo(this, data, { barcodeInfoModel ->

            //
            rlProgressView.visibility = View.GONE
            liDataView.visibility = View.VISIBLE


            imgUser.load(barcodeInfoModel.image) {
                crossfade(true)
                placeholder(R.drawable.avatar)
                error(R.drawable.avatar)
                transformations(CircleCropTransformation())
            }

            //
            userName = barcodeInfoModel.cname
            txtUserName.text = userName

            //
            btnImageSelect.setOnClickListener {
                showImageSourceDialog()
            }


        }, { errorMessage ->
            Commons.showDialog(this, errorMessage ?: "", {
                finish()
            });
        },{

            //
            rlProgressView.visibility = View.GONE
            liDataView.visibility = View.VISIBLE

            //
            imgUser.load("") {
                crossfade(true)
                placeholder(R.drawable.avatar)
                error(R.drawable.avatar)
                transformations(CircleCropTransformation())
            }

            //
            txtUserName.text = "upload a file for $barcodeValue"

            //
            btnImageSelect.setOnClickListener {
                showImageSourceDialog()
            }


        })


    }

    fun registerBgService() {

        initBgProcess(this)

        val dbHelper = MyDatabaseHelper(this)
        val notificationId = System.currentTimeMillis().toInt()
        var isInsertedc = dbHelper.insertData(userName, photoFile?.path ?: "", barcodeValue, 0)
        if (isInsertedc > 0) {
            showToast(this, "Data Saved to the future upload.")
            finish()
        }


    }
}