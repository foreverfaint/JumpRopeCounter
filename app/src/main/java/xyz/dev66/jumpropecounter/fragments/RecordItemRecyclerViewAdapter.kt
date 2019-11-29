package xyz.dev66.jumpropecounter.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xyz.dev66.jumpropecounter.R

import kotlinx.android.synthetic.main.fragment_record_item.view.*
import xyz.dev66.jumpropecounter.models.RecordItem
import java.text.DateFormat.getDateTimeInstance

class RecordItemRecyclerViewAdapter(private val lastRecordItems: List<RecordItem>) :
    RecyclerView.Adapter<RecordItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_record_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lastRecordItems[position]
        holder.tvCreatedAt.text = getDateTimeInstance().format(item.createdAt)
        holder.tvCount.text = item.count.toString()
        with (holder.vRecordItem) {
            tag = item
        }
    }

    override fun getItemCount(): Int = lastRecordItems.count()

    inner class ViewHolder(val vRecordItem: View) : RecyclerView.ViewHolder(vRecordItem) {
        val tvCreatedAt: TextView = vRecordItem.tv_created_at
        val tvCount: TextView = vRecordItem.tv_count
        override fun toString() = super.toString() + " '" + tvCount.text + "'"
    }
}
