package com.bklastai.snacktruck

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

data class Grocery(val name: String, val id: Int, val type: GroceryType, var isSelected: Boolean)

enum class GroceryType {
    Veggie,
    Nonveggie,
    Undefined
}

class GroceriesAdapter(var groceries: Array<Grocery>) : RecyclerView.Adapter<GroceriesAdapter.GroceriesViewHolder>() {
    var filterType: GroceryType? = GroceryType.Undefined
    private var veggieGroceries: Array<Grocery> = emptyArray()
    private var nonveggieGroceries: Array<Grocery> = emptyArray()

    class GroceriesViewHolder(val checkBox: CheckBox) : RecyclerView.ViewHolder(checkBox)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceriesViewHolder {
        return GroceriesViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.grocery_list_item, parent, false) as CheckBox)
    }

    override fun onBindViewHolder(holder: GroceriesViewHolder, position: Int) {
        val items = getItems(filterType)
        holder.checkBox.text = items[position].name
        holder.checkBox.isChecked = items[position].isSelected
        holder.checkBox.setOnClickListener {
            items[position].isSelected = !items[position].isSelected
            holder.checkBox.isChecked = items[position].isSelected
        }
    }

    fun setData(newDataset: ArrayList<Grocery>) {
        // what if some purchasedGroceries are already selected?
        groceries = newDataset.toTypedArray()
        veggieGroceries = newDataset.filter({ it.type == GroceryType.Veggie }).toTypedArray()
        nonveggieGroceries = newDataset.filter({ it.type == GroceryType.Nonveggie }).toTypedArray()
        notifyDataSetChanged()
    }

    fun getItems(type: GroceryType?): Array<Grocery> {
        return when (type) {
            GroceryType.Undefined -> groceries
            GroceryType.Veggie -> veggieGroceries
            GroceryType.Nonveggie -> nonveggieGroceries
            null -> emptyArray()
        }
    }

    override fun getItemCount() = getItems(filterType).size

    fun resetGrocerySelection() {
        for (grocery in groceries) {
            grocery.isSelected = false
        }
        notifyDataSetChanged()
    }

    fun filterGroceries(type: GroceryType?) {
        filterType = type
        notifyDataSetChanged()
    }
}