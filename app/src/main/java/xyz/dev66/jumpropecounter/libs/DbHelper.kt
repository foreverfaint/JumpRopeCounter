package xyz.dev66.jumpropecounter.libs

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DBHelper private constructor(ctx: Context):
    ManagedSQLiteOpenHelper(ctx, DB_NAME, null, 1) {

    init {
        instance = this
    }

    companion object {
        private var instance: DBHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance
            ?: DBHelper(ctx.applicationContext)

        const val DB_NAME = "JumpRope"

        const val TABLE_NAME = "RecordItem"

        const val COLUMN_ID = "id"

        const val COLUMN_CREATED_AT = "created_at"

        const val COLUMN_COUNT = "count"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.createTable(
            TABLE_NAME, true,
            COLUMN_ID to INTEGER + PRIMARY_KEY,
            COLUMN_CREATED_AT to INTEGER,
            COLUMN_COUNT to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.dropTable(TABLE_NAME, true)
    }
}

val Context.database: DBHelper
    get() = DBHelper.getInstance(this)