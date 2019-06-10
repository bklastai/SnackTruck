package com.bklastai.snacktruck

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PurchasedGroceriesAdapter(var purchasedGroceries: Array<Grocery>) :
    RecyclerView.Adapter<PurchasedGroceriesAdapter.PurchasedGroceriesViewHolder>() {

    class PurchasedGroceriesViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasedGroceriesViewHolder {
        return PurchasedGroceriesViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.purchased_grocery_list_item, parent, false) as TextView
        )
    }

    override fun onBindViewHolder(holder: PurchasedGroceriesViewHolder, position: Int) {
        holder.textView.text = purchasedGroceries[position].name
    }

    override fun getItemCount() = purchasedGroceries.size
}