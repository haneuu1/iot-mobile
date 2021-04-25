package com.example.smartdoor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylib.openapi.smartdoorapi.SmartdoorApi
import com.example.mylib.openapi.smartdoorapi.data.MqttDataItem
import com.example.mylib.openapi.smartdoorapi.data.RecordingDataItem
import kotlinx.android.synthetic.main.activity_door_access_record_list.*
import kotlinx.android.synthetic.main.activity_door_access_record_list.NumResult
import kotlinx.android.synthetic.main.activity_door_access_record_list.departure_date
import kotlinx.android.synthetic.main.activity_door_access_record_list.return_date
import kotlinx.android.synthetic.main.activity_video_list.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DoorAccessRecordListActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName
    val mqttList = ArrayList<MqttDataItem>()

    lateinit var startDate: String
    lateinit var endDate: String

    companion object {
        val REQUEST = 0
        val SD = "SD"
        val ED = "ED"
        val RSD = "RSD"
        val RED = "RED"
        val KEY_DATA = "DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_door_access_record_list)

        SmartdoorApi.getMqttList {
            if(it != null) {
                mqttList.addAll(it)
                Log.d(TAG, "first data : ${mqttList[0]}")

                startDate = SimpleDateFormat("yyyy년 MM월 dd일").format(mqttList.get(mqttList.size - 1).timestamp)
                endDate = SimpleDateFormat("yyyy년 MM월 dd일").format(mqttList[0].timestamp)

                rv_door_access_record_list.adapter = DoorAccessRecordListAdapter(mqttList, ::onItemClick)
                rv_door_access_record_list.layoutManager = LinearLayoutManager(this)

                departure_date.text = startDate
                return_date.text = endDate
                NumResult.text = "검색 결과 : ${mqttList.size.toString()} 건"
            }
        }

        departure_date.setOnClickListener {
            val i = Intent(this, RangeDatePickerActivity::class.java)
            i.putExtra(VideoListActivity.SD, startDate)
            i.putExtra(VideoListActivity.ED, endDate)

            startActivityForResult(i, VideoListActivity.REQUEST)
        }
    }

    fun onItemClick(pos: Int) {
        val data = mqttList[pos]
        Log.d(TAG, "topic: ${data.topic}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val request_startDate: Date
        val request_endDate: Date

        if(requestCode != VideoListActivity.REQUEST) return

        val filtered_mqttList = java.util.ArrayList<MqttDataItem>()


        data?.getStringExtra(VideoListActivity.RSD).let{
            departure_date.text = it
            request_startDate = SimpleDateFormat("yyyy년 MM월 dd일").parse(it)
        }

        data?.getStringExtra(VideoListActivity.RED).let{
            return_date.text = it
            request_endDate = SimpleDateFormat("yyyy년 MM월 dd일").parse(it)
        }


        Log.d(TAG, "change date : $request_startDate")

        for(i in 0 .. mqttList.size-1 step(1)) {
            val startDiff = mqttList[i].timestamp.time - request_startDate.time
            val endDiff = mqttList[i].timestamp.time - request_endDate.time
//            Log.d(TAG, "차이: ${diff}")
            if(startDiff >= 0 && endDiff < 86400000) {
                filtered_mqttList.add(mqttList[i])
                Log.d(TAG, "pass: ${mqttList[i].timestamp.toString()}")
            }
        }

        Log.d(TAG, "first filtered data : ${filtered_mqttList.size}")
        Log.d(TAG, "test: ${request_startDate.time - request_endDate.time}")

        rv_door_access_record_list.adapter = DoorAccessRecordListAdapter(filtered_mqttList, ::onItemClick)
        rv_door_access_record_list.layoutManager = LinearLayoutManager(this)

        NumResult.text = "검색 결과 : ${filtered_mqttList.size.toString()} 건"

        super.onActivityResult(requestCode, resultCode, data)
    }
}