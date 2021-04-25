package com.example.smartdoor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylib.openapi.smartdoorapi.SmartdoorApi
import com.example.mylib.openapi.smartdoorapi.data.RecordingDataItem
import kotlinx.android.synthetic.main.activity_video_list.*
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class VideoListActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName
    val videoList = ArrayList<RecordingDataItem>()

    lateinit var startDate: String
    lateinit var endDate: String

    companion object { // 어떤 호출인지 상수로 정의
        val REQUEST = 0
        val SD = "SD"
        val ED = "ED"
        val RSD = "RSD"
        val RED = "RED"
        val KEY_DATA = "DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)

        SmartdoorApi.getRecordingList {

            // 들어온 api 값 리스트로 만들기
            if(it != null) {
                videoList.addAll(it)
//            val startDate = videoList.get(videoList.size - 1).video_timestamp

                startDate = SimpleDateFormat("yyyy년 MM월 dd일").format(videoList.get(videoList.size - 1).video_timestamp)
                endDate = SimpleDateFormat("yyyy년 MM월 dd일").format(videoList[0].video_timestamp)
                Log.d(TAG, "start date = $startDate")

                Log.d(TAG, "first data : ${videoList[0]}")
                rv_video_list.adapter = VideoListAdapter(videoList, ::onItemClick)
                rv_video_list.layoutManager = LinearLayoutManager(this)

                departure_date.text = startDate
                return_date.text = endDate
                NumResult.text = "검색 결과 : ${videoList.size.toString()} 건"
            }
        }

        departure_date.setOnClickListener {
            val i = Intent(this, RangeDatePickerActivity::class.java)
            i.putExtra(SD, startDate)
            i.putExtra(ED, endDate)

            startActivityForResult(i, REQUEST)
        }


    }

    fun onItemClick(pos: Int) {
        val data = videoList[pos]
        Log.d(TAG, "video_root: ${data.video_root.toString()}")

        startActivity<VideoPlayActivity>(
                "DATA" to data
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val request_startDate: Date
        val request_endDate: Date

        if(requestCode != REQUEST) return

        val filterd_videoList = ArrayList<RecordingDataItem>()


        data?.getStringExtra(RSD).let{
            departure_date.text = it
            request_startDate = SimpleDateFormat("yyyy년 MM월 dd일").parse(it)
        }

        data?.getStringExtra(RED).let{
            return_date.text = it
            request_endDate = SimpleDateFormat("yyyy년 MM월 dd일").parse(it)
        }


        Log.d(TAG, "change date : $request_startDate")

        for(i in 0 .. videoList.size-1 step(1)) {
            val startDiff = videoList[i].video_timestamp.time - request_startDate.time
            val endDiff = videoList[i].video_timestamp.time - request_endDate.time
//            Log.d(TAG, "차이: ${diff}")
            if(startDiff >= 0 && endDiff < 86400000) {
                filterd_videoList.add(videoList[i])
                Log.d(TAG, "pass: ${videoList[i].video_timestamp.toString()}")
            }
        }

        Log.d(TAG, "first filtered data : ${filterd_videoList.size}")
        Log.d(TAG, "test: ${request_startDate.time - request_endDate.time}")

        rv_video_list.adapter = VideoListAdapter(filterd_videoList, ::onItemClick)
        rv_video_list.layoutManager = LinearLayoutManager(this)

        NumResult.text = "검색 결과 : ${filterd_videoList.size.toString()} 건"

        super.onActivityResult(requestCode, resultCode, data)
    }




}