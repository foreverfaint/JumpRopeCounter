package xyz.dev66.jumpropecounter.fragments

import xyz.dev66.jumpropecounter.libs.getRandomDate
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object RecordContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<RecordItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, RecordItem> = HashMap()

    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createRecord(i))
        }
    }

    private fun addItem(item: RecordItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createRecord(position: Int): RecordItem {
        return RecordItem(
            position.toString(),
            getRandomDate(),
            (80..180).random()
        )
    }

    data class RecordItem(val id: String, val createdAt: Date, val count: Int) {
        override fun toString(): String = "$createdAt => $count"
    }
}
