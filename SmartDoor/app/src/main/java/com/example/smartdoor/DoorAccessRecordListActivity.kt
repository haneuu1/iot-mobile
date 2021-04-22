package com.example.smartdoor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylib.openapi.smartdoorapi.SmartdoorApi
import com.example.mylib.openapi.smartdoorapi.data.MqttDataItem
import kotlinx.android.synthetic.main.activity_door_access_record_list.*

class DoorAccessRecordListActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName
    val mqttList = ArrayList<MqttDataItem>()

    companion object {
        val KEY_DATA = "DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_door_access_record_list)

        SmartdoorApi.getMqttList {
            mqttList.addAll(it)
            Log.d(TAG, "first data : ${mqttList[0]}")

            rv_door_access_record_list.adapter = DoorAccessRecordListAdapter(mqttList, ::onItemClick)
            rv_door_access_record_list.layoutManager = LinearLayoutManager(this)
        }
    }

    fun onItemClick(pos: Int) {
        val data = mqttList[pos]
        Log.d(TAG, "topic: ${data.topic}")
    }
}