import Commons.Companion.createNotificationChannel
import Commons.Companion.uploadData
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UploadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    val appContext = applicationContext



    override fun doWork(): Result {
        try {
            createNotificationChannel(appContext)
            uploadData(appContext);
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }




}
