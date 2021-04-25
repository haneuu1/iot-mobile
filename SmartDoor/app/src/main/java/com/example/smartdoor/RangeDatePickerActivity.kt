package com.example.smartdoor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_range_date_picker.*
import java.text.SimpleDateFormat
import java.util.*

class RangeDatePickerActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_range_date_picker)

        val i = intent ?: return

        val sSD = i.getStringExtra(VideoListActivity.SD)
        val sED = i.getStringExtra(VideoListActivity.ED)

        val startYear = sSD!!.substring(0, 4).toInt()
        val startMonth = sSD!!.substring(6, 8).toInt()
        val startDay = sSD!!.substring(10, 12).toInt()

        val endYear = sED!!.substring(0, 4).toInt()
        val endMonth = sED!!.substring(6, 8).toInt()
        val endDay = sED!!.substring(10, 12).toInt()

        Log.d(TAG, "sSD: ${sSD!!.substring(0, 4).toInt()}")
        Log.d(TAG, "sSD: ${sSD!!.substring(6, 8).toInt()}")
        Log.d(TAG, "sSD: ${sSD!!.substring(10, 12).toInt()}")

        Log.d(TAG, "sED: ${sED!!.substring(0, 4).toInt()}")
        Log.d(TAG, "sED: ${sED!!.substring(6, 8).toInt()}")
        Log.d(TAG, "sED: ${sED!!.substring(10, 12).toInt()}")

        val zeroCalendarDate = Calendar.getInstance()
        zeroCalendarDate.set(2021, endMonth - 1, 1)

        val firstCalendarDate = Calendar.getInstance()
        firstCalendarDate.set(startYear, startMonth-1, startDay)
//        firstCalendarDate.set(2021, endMonth - 1, 1)

        val secondCalendarDate = Calendar.getInstance()
        secondCalendarDate.set(2021, 12, 31)

        val thirdCalendarDate = Calendar.getInstance()
        thirdCalendarDate.set(endYear, endMonth-1, endDay)
//        thirdCalendarDate.set(2021, endMonth - 1, 30)


        calendar_calendar_view.setOnRangeSelectedListener { startDate, endDate, startLabel, endLabel ->
            Log.d(TAG, "startLabel : ${startDate}")

            val simpleStartDate = SimpleDateFormat("yyyy년 MM월 dd일").format(startDate)
            val simpleEndDate = SimpleDateFormat("yyyy년 MM월 dd일").format(endDate)

            calendar_departure_date.text = simpleStartDate
            calendar_return_date.text = simpleEndDate
        }

        calendar_calendar_view.setOnStartSelectedListener { startDate, label ->

            val simpleStartDate = SimpleDateFormat("yyyy년 MM월 dd일").format(startDate)

            calendar_departure_date.text = simpleStartDate
            calendar_return_date.text = simpleStartDate
        }


        calendar_calendar_view.apply {
            setRangeDate(zeroCalendarDate.time, secondCalendarDate.time)
            setSelectionDate(firstCalendarDate.time, thirdCalendarDate.time)
        }

        calendar_btnSearch.setOnClickListener {
            i.putExtra(VideoListActivity.RSD, calendar_departure_date.text.toString())
            i.putExtra(VideoListActivity.RED, calendar_return_date.text.toString())

            setResult(VideoListActivity.REQUEST, i)

            finish()
        }
    }
}