package com.bklastai.snacktruck

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.lang.IllegalStateException

class GroceriesActivity : AppCompatActivity() {
    companion object {
        val groceryServerTypeToClientType = mapOf(
            "Vegetarian" to GroceryType.Veggie,
            "Non-vegetarian" to GroceryType.Nonveggie)
    }

    lateinit var truckId: String
    lateinit var veggieCheckbox: CheckBox
    lateinit var nonveggieCheckbox: CheckBox


    var groceryAdapter = GroceriesAdapter(emptyArray())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groceries)

        truckId = intent?.extras?.getString(INTENT_EXTRA_TRUCK_ID, "") ?: ""
        if (truckId.isEmpty()) { throw IllegalStateException("Truck ID was not found") }
        supportActionBar?.title = truckId

        initViews()
        fetchGroceries()
        startFCMService()// stub method
    }

    private fun initViews() {
        findViewById<RecyclerView>(R.id.groceries_rv)?.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = groceryAdapter
        }
        findViewById<ExtendedFloatingActionButton>(R.id.submit_fab)?.apply { setOnClickListener { submitOrder() } }
        veggieCheckbox = findViewById(R.id.checkbox_veggies)
        nonveggieCheckbox = findViewById(R.id.checkbox_nonveggies)
        veggieCheckbox.setOnCheckedChangeListener { _, _ -> updateFilters() }
        nonveggieCheckbox.setOnCheckedChangeListener { _, _ -> updateFilters() }
    }

    private fun updateFilters() {
        when {
            veggieCheckbox.isChecked && nonveggieCheckbox.isChecked -> groceryAdapter.filterGroceries(GroceryType.Undefined)
            veggieCheckbox.isChecked && !nonveggieCheckbox.isChecked -> groceryAdapter.filterGroceries(GroceryType.Veggie)
            !veggieCheckbox.isChecked && nonveggieCheckbox.isChecked -> groceryAdapter.filterGroceries(GroceryType.Nonveggie)
            !veggieCheckbox.isChecked && !nonveggieCheckbox.isChecked -> groceryAdapter.filterGroceries(null)
        }
    }

    private fun getSelectedGroceries(): ArrayList<Grocery> {
        return getSelectedGroceriesByType(GroceryType.Undefined)
    }

    private fun getSelectedGroceriesByType(type: GroceryType?): ArrayList<Grocery> {
        val selectedProducts = ArrayList<Grocery>()
        for (grocery in groceryAdapter.getItems(type)) {
            if (!grocery.isSelected) continue
            selectedProducts.add(grocery)
        }
        return selectedProducts
    }

    private fun showConfirmationDialog() {
        // IMHO the homework assignment has a bad design, the confirmation dialog should allow user to confirm or deny
        // the grocery selection, but I followed the instructions..

        val dialogView = layoutInflater.inflate(
            R.layout.order_confirmation_dialog_rv, null) as RecyclerView
        dialogView.setHasFixedSize(true)
        dialogView.layoutManager = LinearLayoutManager(this)
        dialogView.adapter = PurchasedGroceriesAdapter(getSelectedGroceries().toTypedArray())

        AlertDialog.Builder(this, R.style.DialogTheme)
            .setTitle(R.string.order_confirmation_dialog_title)
            .setView(dialogView)
            .setOnDismissListener {
                groceryAdapter.resetGrocerySelection()
                veggieCheckbox.isChecked = true
                nonveggieCheckbox.isChecked = true
            }
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun fetchGroceries() {
        // in a complete app, I would make a JSONArrayRequest, if it failed, I would pass en empty array in
        // groceryAdapter.setData and show error text in a TextView located in place of the RecyclerView (which would
        // have to be added in activity_groceries.xml
        //
        // if it the request was successful, then the response would equal the `jsonGroceries` object seen below

        val groceries = ArrayList<Grocery>()
        // here, JSONArray constructor could throw JSONException but not in the real app setup, so I skipped try/catch
        val jsonGroceries = JSONArray(safeGetString(R.string.groceries_jsonarray))

        for (i in 0 until jsonGroceries.length()) {
            val jgrocery: JSONObject? = jsonGroceries.optJSONObject(i)
            val name = jgrocery?.optString("name")
            val typeStr = jgrocery?.optString("type")
            val id: Int? = if (jgrocery?.has("id") != false) jgrocery?.optInt("id") else null
            if (!name.isNullOrEmpty() && !typeStr.isNullOrEmpty() && id != null &&
                groceryServerTypeToClientType.containsKey(typeStr)) {
                groceries.add(Grocery(name, id, groceryServerTypeToClientType[typeStr]!!, false))
            }
        }
        groceryAdapter.setData(groceries)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.groceries_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_item_add_product) {
            showAddProductDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showAddProductDialog() {
        val typeOptions = arrayOf(safeGetString(R.string.vegetarian), safeGetString(R.string.nonvegetarian))
        var type = GroceryType.Undefined
        val edittext = layoutInflater.inflate(R.layout.new_product_name_edittext, null) as EditText

        val dialog = AlertDialog.Builder(this, R.style.DialogTheme)
            .setTitle(R.string.add_product)
            .setSingleChoiceItems(typeOptions, -1) { _, which ->
                type = if (which == 0) GroceryType.Veggie else GroceryType.Nonveggie
            }
            .setView(edittext)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
        dialog.show()

        // hast to be set after dialog is created so we can pass it in maybeAddProduct()
        edittext.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                maybeAddProduct(edittext, type, dialog)
            }
            return@setOnKeyListener true
        }

        // hast to be called after dialog.show() so we prevent dialog dismissal in certain scenarios
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
            maybeAddProduct(edittext, type, dialog)
        }
    }

    private fun maybeAddProduct(edittext: EditText, type: GroceryType, dialog: AlertDialog) {
        when {
            edittext.text.isNullOrEmpty() -> toast(safeGetString(R.string.name_missing))
            type == GroceryType.Undefined -> toast(safeGetString(R.string.type_missing))
            else -> {
                addProduct(edittext.text.toString(), type)
                dialog.dismiss()
            }
        }
    }

    private fun addProduct(name: String, type: GroceryType) {
        val newProduct = Grocery(name, name.hashCode(), type, false)
    }

    private fun submitOrder() {
        // in a real app, I'd submit the order by making a POST request, which would require a truckId and a json payload
        // (see commented out code below). Note that the stubbed out code is meant to exemplify the procedure that I
        // would follow, in reality, RequestQueue would have to be a singleton, the url would be stored elsewhere etc.
        if (getSelectedGroceries().isEmpty()) {
            toast(R.string.submit_no_groceries_error)
            return
        }
        if (groceryAdapter.filterType != GroceryType.Undefined &&
            getSelectedGroceriesByType(groceryAdapter.filterType).size != getSelectedGroceries().size) {
            AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle(R.string.warning_dialog_title)
                .setMessage(R.string.warning_dialog_message)
                .setNegativeButton(R.string.warning_dialog_proceed_anyway) { _, _ ->
                    showConfirmationDialog() }
                .setPositiveButton(R.string.warning_dialog_show_full_list) { _, _ ->
                    veggieCheckbox.isChecked = true
                    nonveggieCheckbox.isChecked = true }
                .show()
        } else {
            showConfirmationDialog()
        }

//        val selectedProducts = getSelectedGroceries()
//        val payload = getOrderPayload(selectedProducts)
//
//        val url = String.format("http://groceryorder.snacktruck.com/truck/%s/placeOrder", truckId)
//        val submitOrderRequest = JsonObjectRequest(Request.Method.POST, url, payload,
//            Response.Listener<JSONObject> {
//                showConfirmationDialog()
//            },
//            Response.ErrorListener {
//                toast(R.string.submit_request_error)
//            })
//        Volley.newRequestQueue(this).add(submitOrderRequest)// all commented out code is hypothetical but it's
    }

    private fun startFCMService() {
        // this is a stub to indicate that, in a real app, I would:
        //
        // start FirebaseMessagingService passing in the `truckId`, to configure the service to only listen to updates
        // to the grocery list of this specific truck.
        //
        // When the notification is received, we would call GroceriesActivity.fetchGroceries() again.
    }

    // this method is used in the stubbed out code in submitOrder, so I did not delete it
    private fun getOrderPayload(selectedProducts: ArrayList<Grocery>): JSONObject {
        var accumulatedProductIdsAsJson = "{\"productIds\"=["
        for (count in 0 until selectedProducts.size) {
            val product = selectedProducts[count]
            accumulatedProductIdsAsJson += (product.id.toString() +
                    if (count != selectedProducts.lastIndex) "," else "]}")
        }
        return JSONObject(accumulatedProductIdsAsJson)
    }
}