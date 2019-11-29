package xyz.dev66.jumpropecounter.models

import android.content.ContentValues
import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.select
import xyz.dev66.jumpropecounter.libs.DBHelper
import xyz.dev66.jumpropecounter.libs.database
import java.util.*

@Parcelize
data class RecordItem(val id: Long, val createdAt: Date, val count: Int): Parcelable {
    override fun toString(): String = "$createdAt => $count"

    companion object {
        fun add(context: Context, count: Int) = context.database.use {
            val createdAt = Date()
            val contentValues = ContentValues()
            contentValues.put(DBHelper.COLUMN_CREATED_AT, createdAt.time)
            contentValues.put(DBHelper.COLUMN_COUNT, count)
            insert(DBHelper.TABLE_NAME, null, contentValues)
        }

        fun fetchLastRecordItems(context: Context, size: Int): List<RecordItem> = context.database.use {
            select(DBHelper.TABLE_NAME)
                .columns(DBHelper.COLUMN_ID, DBHelper.COLUMN_CREATED_AT, DBHelper.COLUMN_COUNT)
                .orderBy(DBHelper.COLUMN_ID, SqlOrderDirection.DESC)
                .limit(size)
                .parseList(object: MapRowParser<RecordItem> {
                    override fun parseRow(columns: Map<String, Any?>): RecordItem {
                        val id = columns.getValue(DBHelper.COLUMN_ID) as Long
                        val createdAt = columns.getValue(DBHelper.COLUMN_CREATED_AT) as Long
                        val count = columns.getValue(DBHelper.COLUMN_COUNT) as Long
                        return RecordItem(id, Date(createdAt), count.toInt())
                    }
                })
        }
    }
}