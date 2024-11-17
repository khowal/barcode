import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "example.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "BarcodeData"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_NAME = "user_name"
        private const val COLUMN_USER_IMAGE_PATH = "user_image_path"
        private const val COLUMN_USER_BARCODE = "user_barcode"
        private const val COLUMN_STATUS = "status" // 1 - > Done , 0 -> remaining
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME (" + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "$COLUMN_USER_NAME TEXT," + "$COLUMN_USER_IMAGE_PATH TEXT," + "$COLUMN_USER_BARCODE TEXT," + "$COLUMN_STATUS INTEGER" + ")"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(name: String, path: String, barcode: String, status: Int): Long {
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, name)
            put(COLUMN_USER_IMAGE_PATH, path)
            put(COLUMN_USER_BARCODE, barcode)
            put(COLUMN_STATUS, status)
        }
        val db = writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }


    fun fetchAllData(): List<Map<String, Any>> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        val dataList = mutableListOf<Map<String, Any>>()

        if (cursor.moveToFirst()) {
            do {

                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val user_name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME))
                val image_path =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_IMAGE_PATH))
                val barcode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BARCODE))
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS))


                dataList.add(
                    mapOf(
                        COLUMN_ID to id,
                        COLUMN_USER_NAME to user_name,
                        COLUMN_USER_IMAGE_PATH to image_path,
                        COLUMN_USER_BARCODE to barcode,
                        COLUMN_STATUS to status,
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close() // Always close the cursor to avoid memory leaks
        db.close()
        return dataList
    }


    fun clearTable() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.close()
    }

    fun updateUser(id: Int, status: Int) {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_STATUS, status)
        }
        db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }


}
