package com.example.apptecnicatura
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 19
        private const val TABLE_USERS = "User"
        private const val TABLE_SOCIOS = "Socios"
        private const val TABLE_NO_SOCIOS = "NoSocios"

        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_PAID = "paid"
        private const val COLUMN_SOCIO_ID = "socioId"
        private const val COLUMN_NO_SOCIO_ID ="noSocioId"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_INSCRIPTION_DATE = "inscriptiondate"
        private const val COLUMN_PAYMENT_DATE = "paymentdate"
        private const val COLUMN_DUEDATE = "duedate"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = "CREATE TABLE $TABLE_USERS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USERNAME TEXT UNIQUE, $COLUMN_PASSWORD TEXT, $COLUMN_SOCIO_ID INTEGER, $COLUMN_NO_SOCIO_ID INTEGER, FOREIGN KEY($COLUMN_SOCIO_ID) REFERENCES $TABLE_SOCIOS($COLUMN_ID),FOREIGN KEY($COLUMN_NO_SOCIO_ID) REFERENCES $TABLE_NO_SOCIOS($COLUMN_ID),CHECK (( $COLUMN_SOCIO_ID IS NOT NULL AND $COLUMN_NO_SOCIO_ID IS NULL) OR ( $COLUMN_SOCIO_ID IS NULL AND $COLUMN_NO_SOCIO_ID IS NOT NULL)))"
        val createSociosTable = "CREATE TABLE $TABLE_SOCIOS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USERNAME TEXT UNIQUE, $COLUMN_PASSWORD TEXT, $COLUMN_NAME TEXT, $COLUMN_AGE INTEGER, $COLUMN_INSCRIPTION_DATE TEXT, $COLUMN_PAYMENT_DATE TEXT,$COLUMN_DUEDATE TEXT, $COLUMN_PAID INTEGER)"
        val createNoSociosTable = "CREATE TABLE $TABLE_NO_SOCIOS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USERNAME TEXT UNIQUE, $COLUMN_PASSWORD TEXT, $COLUMN_NAME TEXT, $COLUMN_AGE INTEGER,$COLUMN_INSCRIPTION_DATE TEXT, $COLUMN_PAYMENT_DATE TEXT,$COLUMN_DUEDATE TEXT, $COLUMN_PAID INTEGER)"

        db.execSQL(createUserTable)
        db.execSQL(createSociosTable)
        db.execSQL(createNoSociosTable)

        val insertAdminUser = "INSERT INTO $TABLE_USERS ($COLUMN_USERNAME, $COLUMN_PASSWORD, $COLUMN_SOCIO_ID) VALUES ('admin', 'admin', 9999)"
        db.execSQL(insertAdminUser)


    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SOCIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NO_SOCIOS")

        onCreate(db)
    }

    fun validateUser(username: String, password: String): Long? {
        val db = readableDatabase
        Log.d("DatabaseHelper", "Validating login for username: $username")

        val cursor = db.rawQuery("SELECT id FROM user WHERE username = ? AND password = ?", arrayOf(username, password))

        var userId: Long? = null
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            Log.d("DatabaseHelper", "Login successful, userId: $userId")
        } else {
            Log.d("DatabaseHelper", "Login failed for username: $username")
        }
        cursor.close()
        db.close()
        return userId
    }

    fun insertSocio(username: String, password: String, name:String, age: Int, inscriptiondate: String ): Boolean {
        val db =writableDatabase

        val cursor = db.query(
            TABLE_SOCIOS,
            arrayOf(COLUMN_USERNAME),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        val userExists = cursor.count > 0
        cursor.close()

        if (userExists) {
            db.close()
            return false // Nombre de usuario ya existe
        }

        val contentValues = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NAME, name)
            put(COLUMN_AGE, age)
            put(COLUMN_INSCRIPTION_DATE, inscriptiondate)

            put(COLUMN_PAID, 0)
        }

        val socioId = db.insert(TABLE_SOCIOS, null,contentValues)

        if (socioId != -1L) {
            val userValues = ContentValues().apply {
                put(COLUMN_USERNAME, username)
                put(COLUMN_PASSWORD, password)
                put(COLUMN_SOCIO_ID, socioId)
            }
            db.insert(TABLE_USERS, null, userValues)
            db.close()
            return true // Inserción exitosa
        } else {
            db.close()
            return false // Fallo en la inserción
        }
    }

    fun insertNoSocio(username: String, password: String, name:String, age: Int, inscriptiondate: String ): Boolean {
        val db =writableDatabase

        val cursor = db.query(
            TABLE_NO_SOCIOS,
            arrayOf(COLUMN_USERNAME),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        val userExists = cursor.count > 0
        cursor.close()

        if (userExists) {
            db.close()
            return false // Nombre de usuario ya existe
        }

        val contentValues = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NAME, name)
            put(COLUMN_AGE, age)
            put(COLUMN_INSCRIPTION_DATE, inscriptiondate)
            put(COLUMN_PAID, 0)
        }

        val noSocioId = db.insert(TABLE_NO_SOCIOS, null, contentValues)

        if (noSocioId != -1L) {
            val userValues = ContentValues().apply {
                put(COLUMN_USERNAME, username)
                put(COLUMN_PASSWORD, password)
                put(COLUMN_NO_SOCIO_ID, noSocioId)
            }
            db.insert(TABLE_USERS, null, userValues)
            db.close()
            return true // Inserción exitosa
        } else {
            db.close()
            return false // Fallo en la inserción
        }
        }



    fun updatePaidStatus(userId: String?, paid: Boolean, paymentDate: String, dueDate: String, tableName: String): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("paid", if (paid) 1 else 0) // Convertimos el booleano a entero
            put("paymentdate", paymentDate) // Añadimos el nuevo valor
            put("duedate", dueDate)
        }

        // La cláusula WHERE para identificar el registro que quieres actualizar
        val selection = "id = ?"
        val selectionArgs = arrayOf(userId.toString())

        db.beginTransaction()
        return try {
            val rowsUpdated = db.update(tableName, contentValues, selection, selectionArgs)
            db.setTransactionSuccessful()
            println("updatePaidStatus: Updated $rowsUpdated rows in $tableName for userId $userId")
            rowsUpdated
        } catch (e: Exception) {
            println("updatePaidStatus: Error updating $tableName for userId $userId - ${e.message}")
            0
        } finally {
            db.endTransaction()
        }
    }



    fun getUserDetails(userId: Long): Map<String, Any>? {
        val db = this.readableDatabase
        var userDetails: Map<String, Any>? = null

        val cursor = db.rawQuery("SELECT * FROM user WHERE id = ?", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            val socioId = cursor.getLong(cursor.getColumnIndexOrThrow("socioId"))
            val noSocioId = cursor.getLong(cursor.getColumnIndexOrThrow("noSocioId"))

            if (socioId != 0L) {
                val userCursor = db.rawQuery("SELECT * FROM Socios WHERE id = ?", arrayOf(socioId.toString()))
                if (userCursor.moveToFirst()) {
                    userDetails = mapOf(
                        "id" to userCursor.getInt(userCursor.getColumnIndexOrThrow("id")),
                        "username" to userCursor.getString(userCursor.getColumnIndexOrThrow("username")),
                        "password" to userCursor.getString(userCursor.getColumnIndexOrThrow("password")),
                        "name" to userCursor.getString(userCursor.getColumnIndexOrThrow("name")),
                        "age" to userCursor.getInt(userCursor.getColumnIndexOrThrow("age")),
                        "inscriptiondate" to userCursor.getString(userCursor.getColumnIndexOrThrow("inscriptiondate")),
                        "paymentdate" to userCursor.getString(userCursor.getColumnIndexOrThrow("paymentdate")),
                        "duedate" to userCursor.getString(userCursor.getColumnIndexOrThrow("duedate")),
                        "paid" to (userCursor.getInt(userCursor.getColumnIndexOrThrow("paid")) != 0)
                    )
                }
                userCursor.close()
            } else if (noSocioId != 0L) {
                val userCursor = db.rawQuery("SELECT * FROM Nosocios WHERE id = ?", arrayOf(noSocioId.toString()))
                if (userCursor.moveToFirst()) {
                    userDetails = mapOf(
                        "id" to userCursor.getInt(userCursor.getColumnIndexOrThrow("id")),
                        "username" to userCursor.getString(userCursor.getColumnIndexOrThrow("username")),
                        "password" to userCursor.getString(userCursor.getColumnIndexOrThrow("password")),
                        "name" to userCursor.getString(userCursor.getColumnIndexOrThrow("name")),
                        "age" to userCursor.getInt(userCursor.getColumnIndexOrThrow("age")),
                        "inscriptiondate" to userCursor.getString(userCursor.getColumnIndexOrThrow("inscriptiondate")),
                        "paymentdate" to userCursor.getString(userCursor.getColumnIndexOrThrow("paymentdate")),
                        "duedate" to userCursor.getString(userCursor.getColumnIndexOrThrow("duedate")),
                        "paid" to (userCursor.getInt(userCursor.getColumnIndexOrThrow("paid")) != 0)

                    )
                }
                userCursor.close()
            }
        }
        cursor.close()

        return userDetails
    }

    fun getNoSocioId(userId: Long): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT noSocioId FROM user WHERE id = ?", arrayOf(userId.toString()))
        println(cursor)
        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow("noSocioId"))
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    fun getList(): List<List<String>> {
        val datos: MutableList<List<String>> = mutableListOf()

        val db = this.readableDatabase
        val currentDate = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_DATE).plusMonths(1).toString()
        val sql = "SELECT * FROM Socios WHERE duedate = ? UNION ALL SELECT * FROM NoSocios WHERE duedate = ?"
        val cursor: Cursor = db.rawQuery(sql, arrayOf(currentDate))

        while (cursor.moveToNext()) {
            val fila: MutableList<String> = mutableListOf()
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
            fila.add(cursor.getString(cursor.getColumnIndexOrThrow("duedate")))
            datos.add(fila)
        }
        cursor.close()
        return datos
    }


}


