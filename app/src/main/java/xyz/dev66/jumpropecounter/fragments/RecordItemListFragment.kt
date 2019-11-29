package xyz.dev66.jumpropecounter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import xyz.dev66.jumpropecounter.R
import xyz.dev66.jumpropecounter.models.RecordItem


class RecordItemListFragment : Fragment() {

    private val recordItemList = arrayListOf<RecordItem>()

    private lateinit var recordItemAdapter : RecordItemRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record_item_list, container, false)
        if (view is RecyclerView) {
            recordItemAdapter = RecordItemRecyclerViewAdapter(recordItemList)

            with(view) {
                layoutManager = LinearLayoutManager(context)

                adapter = recordItemAdapter

                addItemDecoration(
                    DividerItemDecoration(
                        view.getContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }

            notifyDataSetChanged()
        }
        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            notifyDataSetChanged()
        }
    }

    private fun notifyDataSetChanged() {
        doAsync {
            recordItemList.clear()
            recordItemList.addAll(RecordItem.fetchLastRecordItems(context!!, 50))
            this@RecordItemListFragment.context!!.runOnUiThread {
                recordItemAdapter.notifyDataSetChanged()
            }
        }
    }
}
