import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.barcode.R


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

    }
}