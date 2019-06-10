package com.bklastai.snacktruck.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bklastai.snacktruck.R
import com.bklastai.snacktruck.activities.GroceriesActivity

const val INTENT_EXTRA_TRUCK_ID = "com.bklastai.snacktruck.adapters.INTENT_EXTRA_TRUCK_ID"

class TruckListAdapter(private val myDataset: Array<String>) :
    RecyclerView.Adapter<TruckListAdapter.TruckViewHolder>() {

    inner class TruckViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruckViewHolder {
        return TruckViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.truck_list_item, parent, false) as TextView
        )
    }

    override fun onBindViewHolder(holder: TruckViewHolder, position: Int) {
        holder.textView.text = myDataset[position]
        val intent = Intent(holder.textView.context, GroceriesActivity::class.java).apply {
            putExtra(INTENT_EXTRA_TRUCK_ID, myDataset[position])
        }
        holder.textView.setOnClickListener {
            holder.textView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = myDataset.size
}