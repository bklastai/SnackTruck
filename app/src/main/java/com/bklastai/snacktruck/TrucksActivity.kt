package com.bklastai.snacktruck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TrucksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trucks)

        findViewById<RecyclerView>(R.id.truck_rv).apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = TruckListAdapter(listOf("Truck 1", "Truck 2", "Truck 3").toTypedArray())
        }
    }
}
