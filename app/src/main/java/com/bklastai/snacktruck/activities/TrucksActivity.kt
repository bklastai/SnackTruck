package com.bklastai.snacktruck.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bklastai.snacktruck.R
import com.bklastai.snacktruck.adapters.TruckListAdapter

class TrucksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trucks)

        findViewById<RecyclerView>(R.id.truck_rv)?.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = TruckListAdapter(
                listOf(
                    "Pike's Place",
                    "Capitol Hill",
                    "U District"
                ).toTypedArray()
            )
        }
    }
}
