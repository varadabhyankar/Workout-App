import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutapplicationv1.MainActivity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

// Step 1: Create a SQLiteOpenHelper class
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(FoodItemContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(FoodItemContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FoodItems.db"
    }
}

// Step 2: Define a contract class
object FoodItemContract {
    // Define table structure
    object FoodItemEntry : BaseColumns {
        const val TABLE_NAME = "food_items"
        const val COLUMN_NAME = "name"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_PROTEIN = "protein"
        const val COLUMN_CARBS = "carbs"
        const val COLUMN_SUGAR = "sugar"
        const val COLUMN_CHOLESTEROL = "cholesterol"
        const val COLUMN_FIBER = "fiber"
        const val COLUMN_FAT = "fat"
        const val COLUMN_CALORIES = "calories"
    }

    // SQL commands for creating and deleting the table
    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${FoodItemEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FoodItemEntry.COLUMN_NAME} TEXT," +
                "${FoodItemEntry.COLUMN_QUANTITY} REAL," +
                "${FoodItemEntry.COLUMN_PROTEIN} REAL," +
                "${FoodItemEntry.COLUMN_CARBS} REAL," +
                "${FoodItemEntry.COLUMN_SUGAR} REAL," +
                "${FoodItemEntry.COLUMN_CHOLESTEROL} REAL," +
                "${FoodItemEntry.COLUMN_FIBER} REAL," +
                "${FoodItemEntry.COLUMN_FAT} REAL," +
                "${FoodItemEntry.COLUMN_CALORIES} REAL)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FoodItemEntry.TABLE_NAME}"
}

class CSVReader(private val context: Context) {
    fun readAndInsertCSV() {
        try {
            val dbHelper = DatabaseHelper(context)
            val db = dbHelper.writableDatabase

            // Read CSV file from the assets folder
            val inputStream: InputStream = context.assets.open("nutrition.csv")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            // Skip the header row
            bufferedReader.readLine()

            // Read each line of the CSV file
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                // Split the line into individual values
                val values = line?.split(",") ?: continue

                // Create a ContentValues object to store the data
                val contentValues = ContentValues().apply {
                    put(FoodItemContract.FoodItemEntry.COLUMN_NAME, values[0])
                    put(FoodItemContract.FoodItemEntry.COLUMN_QUANTITY, values[1].toDouble())
                    put(FoodItemContract.FoodItemEntry.COLUMN_PROTEIN, values[2].toDouble())
                    put(FoodItemContract.FoodItemEntry.COLUMN_CARBS, values[3].toDouble())
                    put(FoodItemContract.FoodItemEntry.COLUMN_SUGAR, values[4].toDouble())
                    put(FoodItemContract.FoodItemEntry.COLUMN_CHOLESTEROL, values[5].toDouble())
                    put(FoodItemContract.FoodItemEntry.COLUMN_FIBER, values[6].toDouble())
                    put(FoodItemContract.FoodItemEntry.COLUMN_FAT, values[7].toDouble())
                    put(FoodItemContract.FoodItemEntry.COLUMN_CALORIES, values[8].toDouble())
                }

                // Insert the data into the database
                db.insert(FoodItemContract.FoodItemEntry.TABLE_NAME, null, contentValues)

            }

            bufferedReader.close()
            dbHelper.close()
        } catch (e: Exception) {
            Log.e("CSVReader", "Error reading and inserting CSV: ${e.message}")
        }
    }
}