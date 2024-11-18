import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.barcode.R
import com.example.barcode.Receivers.NetworkMonitoringService
import java.io.File
import java.util.concurrent.TimeUnit


class Commons {

    companion object {

        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
            return permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }

        fun showDialog(context: Context, msg: String, callBackButton: () -> Unit) {
            lateinit var builder: AlertDialog.Builder
            builder = AlertDialog.Builder(context);
            builder.setTitle(R.string.app_name)
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setMessage(msg)

            builder.setPositiveButton(R.string.close) { dialogInterface, which ->
                dialogInterface.dismiss()
                callBackButton();
            }

            builder.show();
        }

        fun optionDialog(
            context: Context,
            msg: String,
            postiveButtonText: String,
            negetiveButtonText: String,
            neutralButtonText: String,
            callBackPostive: () -> Unit,
            callBackNegative: () -> Unit
        ) {
            lateinit var builder: AlertDialog.Builder
            builder = AlertDialog.Builder(context);
            builder.setTitle(R.string.app_name)
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setMessage(msg)

            builder.setPositiveButton(postiveButtonText) { dialogInterface, which ->
                dialogInterface.dismiss()
                callBackPostive();
            }

            builder.setNegativeButton(negetiveButtonText) { dialogInterface, which ->
                dialogInterface.dismiss()
                callBackNegative();
            }
            builder.setNeutralButton(neutralButtonText) { dialogInterface, which ->
                dialogInterface.dismiss()
            }

            builder.show();
        }

        fun initBgProcess(context: Context) {

            val uniqueWorkName = "barcodeUniqueWork"

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val uploadRequest = PeriodicWorkRequestBuilder<UploadWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()


            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.UPDATE,
                uploadRequest
            )


//
//            val serviceIntent = Intent(context, NetworkMonitoringService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent)
//            } else {
//                context.startActivity(serviceIntent)
//            }

        }


        fun askForNotificationPermission(
            context: Activity,
            granted: () -> Unit,
            askForPermission: () -> Unit
        ) {
            // Check if the app already has notification permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    askForPermission()
                } else {
                    granted()

                }
            } else {
                granted()

            }

        }

        fun getRealPathFromURI(context: Context, uri: Uri): String? {
            val contentResolver: ContentResolver = context.contentResolver
            var filePath: String? = null

            if (uri.scheme == "content") {
                // Handle content URI (e.g., content://)
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        filePath = it.getString(columnIndex)
                    }
                }
            } else if (uri.scheme == "file") {
                // Handle file URI (e.g., file://)
                filePath = uri.path
            }

            return filePath
        }

        fun uploadData(context: Context) {

            //
            createNotificationChannel(context)

            //
            val dbHelper = MyDatabaseHelper(context)
            var apiController: ApiController = ApiController()

            var list = dbHelper.fetchRemainingUploads(true);
            Log.i("from workerr", "" + list.size)
            if (list.size > 0) {
                var dbData = list[0];
                var id = dbData.get(MyDatabaseHelper.COLUMN_ID) as Int
                var rollNumber = dbData.get(MyDatabaseHelper.COLUMN_USER_BARCODE) as String
                var path = dbData.get(MyDatabaseHelper.COLUMN_USER_IMAGE_PATH) as String
                var name = dbData.get(MyDatabaseHelper.COLUMN_USER_NAME) as String

                var file = File(path);
                Log.e("from workerr111", "" + file.exists())
                Log.e("from workerr", "" + id + ": $path" + ": $name")
                if (file.exists()) {
                    val data = mapOf(
                        AppStrings.PARAM_ROLL_NUMBER to rollNumber,
                        AppStrings.PARAM_PHOTO to file,
                        AppStrings.PARAM_NAME to name,
                    )
                    apiController.uploadBarcodeInfo(context, data, { successResonse ->

                        dbHelper.updateUser(id, 1)
                        showNotification(context, "Image uploaded for roll number $rollNumber");
                        val intent = Intent(AppStrings.BROADCAST_UPDATE_UI)
                        intent.putExtra("message", "update")
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

                        uploadData(context)

                    }, { failureResponse ->
                        dbHelper.updateUser(id, -1)
                        uploadData(context)

                    }, {

                    }, false)
                } else {
                    dbHelper.updateUser(id, -1)
                    uploadData(context)
                }
            }
        }

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "my_channel_id_upload"
                val channelName = "upload_image"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)

                // Register the channel with the system
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun showNotification(context: Context, msg: String) {
            val channelId = "my_channel_id_upload"

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            // Get the NotificationManager system service
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Show the notification with a unique ID
            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notification)
        }

    }
}