package com.bklastai.snacktruck.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bklastai.snacktruck.R
import java.lang.IllegalStateException

// in a real app grocery ID would be computed by the server and would not appear in the constructor
data class Grocery(val name: String, val id: Int, val type: GroceryType, var isSelected: Boolean)

enum class GroceryType {
    Veggie,
    Nonveggie,
    Undefined
}

class GroceriesAdapter(var groceries: ArrayList<Grocery>) :
    RecyclerView.Adapter<GroceriesAdapter.GroceriesViewHolder>() {

    var filterType: GroceryType? = GroceryType.Undefined
    private var veggieGroceries: ArrayList<Grocery> = ArrayList()
    private var nonveggieGroceries: ArrayList<Grocery> = ArrayList()

    inner class GroceriesViewHolder(val checkBox: CheckBox) : RecyclerView.ViewHolder(checkBox)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceriesViewHolder {
        return GroceriesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.grocery_list_item, parent, false) as CheckBox
        )
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

    fun setData(newGroceries: ArrayList<Grocery>) {
        synchronized(groceries) {
            // reset groceries preserving the selection state
            val oldGroceries = groceries
            groceries.clear()
            for (grocery in newGroceries) {
                val existingGroceryWithSameId = oldGroceries.find { gr -> gr.id == grocery.id }
                if (existingGroceryWithSameId == null) {
                    groceries.add(grocery)
                } else {
                    groceries.add(existingGroceryWithSameId)
                }
            }
            veggieGroceries = groceries.filter { it.type == GroceryType.Veggie } as ArrayList<Grocery>
            nonveggieGroceries = groceries.filter { it.type == GroceryType.Nonveggie } as ArrayList<Grocery>
        }
        notifyDataSetChanged()
    }

    fun getItems(type: GroceryType?): ArrayList<Grocery> {
        return when (type) {
            GroceryType.Undefined -> groceries
            GroceryType.Veggie -> veggieGroceries
            GroceryType.Nonveggie -> nonveggieGroceries
            null -> ArrayList()
        }
    }

    override fun getItemId(position: Int): Long {
        if (position < getItems(filterType).size) {
            return getItems(filterType)[position].id.toLong()
        } else {
            throw IllegalStateException("position >= items.size")
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