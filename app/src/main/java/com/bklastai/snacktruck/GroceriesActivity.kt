package com.bklastai.snacktruck

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    val requestQueue = Volley.newRequestQueue(this)

    var groceryAdapter = GroceriesAdapter(ArrayList<Grocery>().toTypedArray())

//    lateinit var submitFAB: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groceries)

        truckId = intent?.extras?.getString(INTENT_EXTRA_TRUCK_ID, "") ?: ""
        if (truckId.isEmpty()) { throw IllegalStateException("Truck ID was not found") }
        putStringPref(R.string.key_device_id, truckId)

        findViewById<RecyclerView>(R.id.groceries_rv).apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = groceryAdapter
        }
        findViewById<FloatingActionButton>(R.id.submit_fab).apply {
            setOnClickListener { submitOrder() }
        }

        fetchGroceries()
        startFCMService(this)
    }

    private fun submitOrder() {
        // in a real app, I'd submit the order making a POST request, which would require a truckId and a json payload
        // (see commented out code for details)
        if (getSelectedGroceries().isEmpty()) {
            toast(R.string.submit_no_groceries_error)
            return
        }
        showConfirmationDialog()
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
//        requestQueue.add(submitOrderRequest)
    }

    private fun getOrderPayload(selectedProducts: ArrayList<Grocery>): JSONObject {
        var accumulatedProductIdsAsJson = "{\"productIds\"=["
        for (count in 0 until selectedProducts.size) {
            val product = selectedProducts[count]
            accumulatedProductIdsAsJson += (product.id.toString() +
                    if (count != selectedProducts.lastIndex) "," else "]}")
        }
        return JSONObject(accumulatedProductIdsAsJson)
    }

    private fun getSelectedGroceries(): ArrayList<Grocery> {
        val selectedProducts = ArrayList<Grocery>()
        for (grocery in groceryAdapter.groceries) {
            if (!grocery.isSelected) continue
            selectedProducts.add(grocery)
        }
        return selectedProducts
    }

    private fun showConfirmationDialog() {
//        val builder = AlertDialog.Builder(this).
    }

    private fun startFCMService(groceriesActivity: GroceriesActivity) {
        // this is a stub to indicate that, in a real app, I would:
        //
        // start FirebaseMessagingService passing in the `truckId`, to configure the service to only listen to updates
        // to the grocery list of this specific truck.
        //
        // When the notification is received, we would call GroceriesActivity.fetchGroceries() again.
    }

    private fun fetchGroceries() {
        // in a complete app, I would make a JSONArrayRequest, if it failed, I would pass en empty array in
        // groceryAdapter.setValues and show error text in a TextView located in place of the RecyclerView (which would
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
        groceryAdapter.setValues(groceries.toTypedArray())
    }

    fun onFilterCheckboxClicked(v: View) {
        when {
            v.id == R.id.checkbox_veggies -> {}
            v.id == R.id.checkbox_nonveggies -> {}
        }
    }
}