package com.bklastai.snacktruck

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

data class Grocery(val name: String, val id: Int, val type: GroceryType, var isSelected: Boolean)

enum class GroceryType {
    Veggie,
    Nonveggie
}

class GroceriesAdapter(var groceries: Array<Grocery>) : RecyclerView.Adapter<GroceriesAdapter.GroceriesViewHolder>() {

    class GroceriesViewHolder(val checkBox: CheckBox) : RecyclerView.ViewHolder(checkBox)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceriesViewHolder {
        return GroceriesViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.truck_list_item, parent, false) as CheckBox)
    }

    override fun onBindViewHolder(holder: GroceriesViewHolder, position: Int) {
        holder.checkBox.text = groceries[position].name
        holder.checkBox.isChecked = groceries[position].isSelected
        holder.checkBox.setOnClickListener {
            groceries[position].isSelected = !groceries[position].isSelected
            holder.checkBox.isChecked = groceries[position].isSelected
        }
    }

    fun setValues(newDataset: Array<Grocery>) {
        // what if some groceries are already selected?
        groceries = newDataset
        notifyDataSetChanged()
    }

    override fun getItemCount() = groceries.size
}